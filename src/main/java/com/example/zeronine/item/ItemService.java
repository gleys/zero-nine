package com.example.zeronine.item;

import com.example.zeronine.item.form.ItemForm;
import com.example.zeronine.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    public Item itemCreate(ItemForm form) {
        Item item = modelMapper.map(form, Item.class);
        itemRepository.save(item);

        return item;
    }

    public void editItem(Item item, ItemForm form) {
        Item findItem = itemRepository.findById(item.getId())
                .orElseThrow();
        modelMapper.map(form, findItem);
    }
}
