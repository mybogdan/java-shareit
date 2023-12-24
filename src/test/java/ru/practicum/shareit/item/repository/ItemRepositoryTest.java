package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    Item item1;
    Item item2;
    Item item3;
    ItemRequest request1;
    ItemRequest request2;
    ItemRequest request3;


    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(0L, "test1", "test1@mail.ru"));
        user2 = userRepository.save(new User(0L, "test2", "test2@mail.ru"));

        request1 = itemRequestRepository
                .save(new ItemRequest(null, "testDescription1", user1, LocalDateTime.now()));
        request2 = itemRequestRepository
                .save(new ItemRequest(null, "testDescription2", user1, LocalDateTime.now()));
        request3 = itemRequestRepository
                .save(new ItemRequest(null, "testDescription3", user2, LocalDateTime.now()));

        item1 = itemRepository
                .save(new Item(0L, "testName1", "testDescription1 wow",
                        true, user1, request1));
        item2 = itemRepository
                .save(new Item(0L, "testName2", "testDescription2",
                        true, user2, request2));
        item3 = itemRepository
                .save(new Item(0L, "testName3 wow", "testDescription3",
                        true, user2, request3));
    }

    @Test
    void searchTest() {
        List<Item> results = itemRepository.search("wow");

        Assertions.assertNotNull(results);
        Assertions.assertEquals(2, results.size());
    }

    @Test
    void searchTestWithNoItemsForKeyword() {
        List<Item> results = itemRepository.search("testy");

        Assertions.assertNotNull(results);
        Assertions.assertEquals(0, results.size());
    }

    @Test
    void findAllByOwnerIdOrderByIdAscTest() {
        List<Item> results = itemRepository.findAllByOwnerId(user2.getId());

        Assertions.assertNotNull(results);
        Assertions.assertEquals(2, results.size());
    }

    @Test
    void findAllByRequestIdTest() {
        List<Item> results = itemRepository.findAllByRequest_Id(request1.getId());

        Assertions.assertNotNull(results);
        Assertions.assertEquals(1, results.size());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}