package com.example.zeronine.category;

import com.example.zeronine.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void initCategory() throws IOException {
        if (categoryRepository.count() == 0) {
            Resource resource = new ClassPathResource("품목 분류.csv");
            List<Category> categories = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
                    .map(items -> {
                        String[] split = items.split(",");
                        Category parent = new Category(split[0]);

                        for (int i = 1; i < split.length; i++) {
                            Category child = new Category(split[i]);
                            parent.addChild(child);
                        }
                        return parent;
                    }).collect(Collectors.toList());
            categoryRepository.saveAll(categories);
        }
    }
}
