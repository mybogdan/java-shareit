package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestRepositoryTests {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByRequesterIdOrderByCreatedAscTest() {
        User user = userRepository.save(User.builder().name("name").email("email@email.com").build());
        itemRequestRepository.save(ItemRequest.builder().description("description").requestor(user)
                .created(LocalDateTime.now()).build());
        List<ItemRequest> items = itemRequestRepository.findAllByRequestorId(user.getId());
        assertThat(items.size(), equalTo(1));
    }

    @Test
    void findAllTest() {
        User user = userRepository.save(User.builder().name("name").email("email@email.com").build());
        itemRequestRepository.save(ItemRequest.builder().description("description").requestor(user)
                .created(LocalDateTime.now()).build());
        assertThat(itemRequestRepository.findAllByRequestorId(user.getId())
                .stream().count(), equalTo(1L));
        User user2 = userRepository.save(User.builder().name("name2").email("email2@email.com").build());
        assertThat(itemRequestRepository.findAllByRequestorId(user2.getId())
                .stream().count(), equalTo(0L));
    }
}