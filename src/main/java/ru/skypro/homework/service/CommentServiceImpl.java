package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public Comments getCommentsByAdId(Integer adId) {
        log.info("Получение комментариев для объявления с id: {}", adId);

        List<CommentEntity> entities = commentRepository.findByAdPk(adId);
        List<Comment> commentList = commentMapper.toCommentDtoList(entities);

        Comments comments = new Comments();
        comments.setCount(commentList.size());
        comments.setResults(commentList);

        return comments;
    }

    @Override
    @Transactional
    public Comment addComment(Integer adId, String email, CreateOrUpdateComment createOrUpdateComment) {
        log.info("Добавление комментария к объявлению с id: {} пользователем с email: {}", adId, email);

        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        UserEntity author = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        CommentEntity commentEntity = commentMapper.toCommentEntity(createOrUpdateComment);
        commentEntity.setAd(ad);
        commentEntity.setAuthor(author);
        commentEntity.setCreatedAt(System.currentTimeMillis());

        CommentEntity savedComment = commentRepository.save(commentEntity);

        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Integer adId, Integer commentId, String email) {
        log.info("Удаление комментария с id: {} из объявления с id: {} пользователем с email: {}",
                commentId, adId, email);

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        // Проверяем права на удаление
        checkCommentPermissions(commentEntity, email);

        commentRepository.delete(commentEntity);
    }

    @Override
    @Transactional
    public Comment updateComment(Integer adId, Integer commentId, String email,
                                 CreateOrUpdateComment createOrUpdateComment) {
        log.info("Обновление комментария с id: {} из объявления с id: {} пользователем с email: {}",
                commentId, adId, email);

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        // Проверяем права на редактирование
        checkCommentPermissions(commentEntity, email);

        commentMapper.updateCommentEntityFromDto(createOrUpdateComment, commentEntity);
        CommentEntity updatedComment = commentRepository.save(commentEntity);

        return commentMapper.toCommentDto(updatedComment);
    }

    private void checkCommentPermissions(CommentEntity commentEntity, String email) {
        // Если пользователь не автор и не админ, то выбрасываем исключение
        if (!commentEntity.getAuthor().getEmail().equals(email)) {
            // Проверяем, может быть это админ?
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

            if (!"ADMIN".equals(user.getRole().name())) {
                throw new AccessDeniedException("Нет прав на редактирование этого комментария");
            }
        }
    }
}
