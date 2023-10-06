package tech.fublog.FuBlog.service;

import tech.fublog.FuBlog.dto.response.ResponseCategoryDTO;
import tech.fublog.FuBlog.entity.CategoryEntity;
import tech.fublog.FuBlog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ResponseCategoryDTO> getAllCategory() {
        List<CategoryEntity> list = categoryRepository.findByParentCategoryIsNull();
        if (!list.isEmpty()) {
            List<ResponseCategoryDTO> dtoList = new ArrayList<>();
            for (CategoryEntity entity : list) {
                ResponseCategoryDTO dto = convertCategoryToDTO(entity);
                dtoList.add(dto);
            }
            return dtoList;
        }
        return new ArrayList<>();
    }

    private ResponseCategoryDTO convertCategoryToDTO(CategoryEntity categoryEntity) {
        ResponseCategoryDTO responseCategoryDTO = new ResponseCategoryDTO();
        responseCategoryDTO.setCategoryId(categoryEntity.getId());
        responseCategoryDTO.setCategoryName(categoryEntity.getCategoryName());
        List<CategoryEntity> subCategory = categoryRepository.findByParentCategory(categoryEntity);
        List<ResponseCategoryDTO> subcategoryDTOResponse = new ArrayList<>();
        for (CategoryEntity sub : subCategory) {
            ResponseCategoryDTO subResponseCategoryDTOs = convertCategoryToDTO(sub);
            subcategoryDTOResponse.add(subResponseCategoryDTOs);
        }
        responseCategoryDTO.setSubCategory(subcategoryDTOResponse);
        return responseCategoryDTO;
    }
}
