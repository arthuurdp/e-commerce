package com.arthuurdp.e_commerce.unit;

import com.arthuurdp.e_commerce.modules.category.dtos.CategoryResponse;
import com.arthuurdp.e_commerce.modules.category.dtos.CreateCategoryRequest;
import com.arthuurdp.e_commerce.modules.category.dtos.UpdateCategoryRequest;
import com.arthuurdp.e_commerce.modules.category.entity.Category;
import com.arthuurdp.e_commerce.modules.category.mapper.CategoryMapper;
import com.arthuurdp.e_commerce.modules.product.entity.Product;
import com.arthuurdp.e_commerce.shared.exceptions.BadRequestException;
import com.arthuurdp.e_commerce.shared.exceptions.ResourceNotFoundException;
import com.arthuurdp.e_commerce.modules.category.CategoryService;
import com.arthuurdp.e_commerce.modules.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository repo;
    @Mock private CategoryMapper mapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = new Category("Electronics");
        category.setId(1L);

        categoryResponse = new CategoryResponse(1L, "Electronics");
    }

    @Nested
    @DisplayName("findEntityById()")
    class FindEntityById {

        @Test
        @DisplayName("returns Category entity when it exists")
        void shouldReturnCategory() {
            when(repo.findById(1L)).thenReturn(Optional.of(category));

            Category result = categoryService.findEntityById(1L);

            assertThat(result).isEqualTo(category);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist")
        void shouldThrowWhenNotFound() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.findEntityById(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Category not found");
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("returns paginated CategoryResponse list")
        void shouldReturnPagedCategories() {
            Page<Category> page = new PageImpl<>(List.of(category));

            when(repo.findAll(any(PageRequest.class))).thenReturn(page);
            when(mapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            Page<CategoryResponse> result = categoryService.findAll(0, 10);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(categoryResponse);
        }

        @Test
        @DisplayName("returns empty page when no categories exist")
        void shouldReturnEmptyPage() {
            when(repo.findAll(any(PageRequest.class))).thenReturn(Page.empty());

            Page<CategoryResponse> result = categoryService.findAll(0, 10);

            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("returns CategoryResponse when category exists")
        void shouldReturnCategoryResponse() {
            when(repo.findById(1L)).thenReturn(Optional.of(category));
            when(mapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            CategoryResponse result = categoryService.findById(1L);

            assertThat(result).isEqualTo(categoryResponse);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist")
        void shouldThrowWhenNotFound() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.findById(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Category not found");
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("saves category and returns CategoryResponse")
        void shouldCreateCategorySuccessfully() {
            CreateCategoryRequest req = new CreateCategoryRequest("Electronics");

            when(repo.save(any(Category.class))).thenReturn(category);
            when(mapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            CategoryResponse result = categoryService.create(req);

            assertThat(result).isEqualTo(categoryResponse);
            verify(repo).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("updates name and returns CategoryResponse")
        void shouldUpdateNameSuccessfully() {
            UpdateCategoryRequest req = new UpdateCategoryRequest("Computers");

            when(repo.findById(1L)).thenReturn(Optional.of(category));
            when(repo.save(category)).thenReturn(category);
            when(mapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            CategoryResponse result = categoryService.update(1L, req);

            assertThat(result).isEqualTo(categoryResponse);
            assertThat(category.getName()).isEqualTo("Computers");
        }

        @Test
        @DisplayName("does not update name when request name is null")
        void shouldNotUpdateWhenNameIsNull() {
            UpdateCategoryRequest req = new UpdateCategoryRequest(null);

            when(repo.findById(1L)).thenReturn(Optional.of(category));
            when(repo.save(category)).thenReturn(category);
            when(mapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            categoryService.update(1L, req);

            assertThat(category.getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist")
        void shouldThrowWhenNotFound() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.update(99L, new UpdateCategoryRequest("X"))).isInstanceOf(ResourceNotFoundException.class).hasMessage("Category not found");

            verify(repo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deletes category when it has no products")
        void shouldDeleteSuccessfully() {
            when(repo.findById(1L)).thenReturn(Optional.of(category));

            categoryService.delete(1L);

            verify(repo).delete(category);
        }

        @Test
        @DisplayName("throws BadRequestException when category has associated products")
        void shouldThrowWhenCategoryHasProducts() {
            category.getProducts().add(mock(Product.class));

            when(repo.findById(1L)).thenReturn(Optional.of(category));

            assertThatThrownBy(() -> categoryService.delete(1L)).isInstanceOf(BadRequestException.class).hasMessage("Cannot delete a category that has products associated");

            verify(repo, never()).delete(any());
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when category does not exist")
        void shouldThrowWhenNotFound() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.delete(99L)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Category not found");

            verify(repo, never()).delete(any());
        }
    }
}