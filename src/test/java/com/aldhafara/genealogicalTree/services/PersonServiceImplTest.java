package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.repositories.PersonRepository;
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Rollback
public class PersonServiceImplTest {

    @Autowired
    private PersonRepository personRepository;

    @MockitoSpyBean
    private PersonServiceImpl spyPersonService;

    @Test
    void saveAll_shouldRollbackAllIfOneSaveThrows() {
        PersonDto goodPerson1 = new PersonDto();
        goodPerson1.setFirstName("John");
        goodPerson1.setLastName("Doe");

        PersonDto goodPerson2 = new PersonDto();
        goodPerson2.setFirstName("Jane");
        goodPerson2.setLastName("Doe");

        PersonDto badPerson = new PersonDto();
        badPerson.setFirstName("Fail");

        doThrow(new RuntimeException("Simulated failure"))
                .when(spyPersonService).saveAndReturn(argThat(dto -> "Fail".equals(dto.getFirstName())));

        List<PersonDto> personList = List.of(goodPerson1, badPerson, goodPerson2);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            spyPersonService.saveAll(personList);
        });

        assertThat(ex.getMessage()).isEqualTo("Simulated failure");

        long count = personRepository.count();
        assertThat(count).isEqualTo(0L);
    }
}
