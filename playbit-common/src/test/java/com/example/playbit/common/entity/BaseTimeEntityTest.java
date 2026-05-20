package com.example.playbit.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BaseTimeEntityTest {

    @Test
    void MappedSuperclass_어노테이션이_있다() {
        assertThat(BaseTimeEntity.class.isAnnotationPresent(MappedSuperclass.class)).isTrue();
    }

    @Test
    void AuditingEntityListener가_등록되어_있다() {
        EntityListeners listeners = BaseTimeEntity.class.getAnnotation(EntityListeners.class);
        assertThat(listeners).isNotNull();
        assertThat(listeners.value()).contains(AuditingEntityListener.class);
    }

    @Test
    void createdAt_필드가_CreatedDate이고_수정불가다() throws NoSuchFieldException {
        Field createdAt = BaseTimeEntity.class.getDeclaredField("createdAt");

        assertThat(createdAt.getType()).isEqualTo(LocalDateTime.class);
        assertThat(createdAt.isAnnotationPresent(CreatedDate.class)).isTrue();

        Column column = createdAt.getAnnotation(Column.class);
        assertThat(column.nullable()).isFalse();
        assertThat(column.updatable()).isFalse();
    }

    @Test
    void updatedAt_필드가_LastModifiedDate다() throws NoSuchFieldException {
        Field updatedAt = BaseTimeEntity.class.getDeclaredField("updatedAt");

        assertThat(updatedAt.getType()).isEqualTo(LocalDateTime.class);
        assertThat(updatedAt.isAnnotationPresent(LastModifiedDate.class)).isTrue();

        Column column = updatedAt.getAnnotation(Column.class);
        assertThat(column.nullable()).isFalse();
    }
}
