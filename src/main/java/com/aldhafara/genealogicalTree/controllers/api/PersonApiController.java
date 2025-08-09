package com.aldhafara.genealogicalTree.controllers.api;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.dto.UserDto;
import com.aldhafara.genealogicalTree.services.FamilyServiceImpl;
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/person")
@Tag(name = "Person Controller", description = "Operations related to Person entities")
public class PersonApiController {
    private final PersonServiceImpl personService;
    private final FamilyServiceImpl familyService;

    public PersonApiController(PersonServiceImpl personService, FamilyServiceImpl familyService) {
        this.personService = personService;
        this.familyService = familyService;
    }

    @GetMapping("/aboutme")
    @Operation(
            summary = "Redirect to currently logged user's details",
            description = "Fetches the current user's details based on their authentication context and redirects to the person's detail page.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Redirected to the person details page.",
                            headers = @Header(name = "Location",
                                    description = "URL of the person's details page",
                                    schema = @Schema(type = "string", example = "/person/{id}"))
                    )
            }
    )
    public ResponseEntity<String> aboutMe(@CurrentSecurityContext SecurityContext context) {
        UserDto userDto = (UserDto) context.getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/person/" + userDto.getDetailsId())
                .build();
    }

    @GetMapping("/add/parent/{parentType}/for/{personId}")
    @Operation(
            summary = "Add a parent for a person",
            description = "Creates and assigns a parent of the specified type (mother or father) to a given person, and redirects to the parent's detail page.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Parent added successfully. Redirected to the parent's details page.",
                            headers = @Header(name = "Location", description = "URL of the parent's details page", schema = @Schema(type = "string", example = "/person/{id}"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parent type.",
                            content = @Content(mediaType = "text/plain", examples = {
                                    @ExampleObject(value = "Invalid parent type")
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Person not found.",
                            content = @Content(mediaType = "text/plain", examples = {
                                    @ExampleObject(value = "Person not found")
                            })
                    )
            }
    )
    public ResponseEntity<String> addParent(@PathVariable UUID personId, @PathVariable String parentType) {
        try {
            SexEnum parentSex = determineParentSex(parentType);
            Person person = personService.getById(personId);
            UUID parentId = getParentUuid(person, parentSex);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/person/" + parentId)
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid parent type");
        } catch (PersonNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found");
        }
    }

    @GetMapping("/add/partner/for/{personId}")
    @Operation(
            summary = "Add a partner for a person",
            description = "Creates and assigns a partner for a given person, ensuring the family is updated accordingly, and redirects to the partner's detail page.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Partner added successfully. Redirected to the partner's details page.",
                            headers = @Header(name = "Location", description = "URL of the partner's details page", schema = @Schema(type = "string", example = "/person/{id}"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Person not found.",
                            content = @Content(mediaType = "text/plain", examples = {
                                    @ExampleObject(value = "Person not found")
                            })
                    )
            }
    )
    public ResponseEntity<String> addPartner(@PathVariable UUID personId) {
        Person savedPerson;
        try {
            Person person = personService.getById(personId);
            savedPerson = personService.save(null);
            if (person.getSex() == SexEnum.MALE) {
                familyService.saveFamilyWithoutChildren(person, savedPerson);
            } else {
                familyService.saveFamilyWithoutChildren(savedPerson, person);
            }
        } catch (PersonNotFoundException e) {
            return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/person/" + savedPerson.getId())
                .build();
    }

    private SexEnum determineParentSex(String parentType) {
        return switch (parentType.toLowerCase()) {
            case "mother" -> SexEnum.FEMALE;
            case "father" -> SexEnum.MALE;
            default -> throw new IllegalArgumentException("Invalid parent type");
        };
    }

    private UUID getParentUuid(Person person, SexEnum parentSex) {
        Person mother = createAndSaveParent(SexEnum.FEMALE, person);
        Person father = createAndSaveParent(SexEnum.MALE, person);

        if (person.getFamily() == null || person.getFamily().getId() == null) {
            familyService.saveFamilyWithChild(father, mother, person);
        }

        return parentSex == SexEnum.MALE ? father.getId() : mother.getId();
    }

    private Person createAndSaveParent(SexEnum parentSex, Person child) {
        PersonDto parentModel = new PersonDto();
        parentModel.setSex(parentSex);
        return personService.saveParent(parentModel, child);
    }

    @GetMapping("/add/sibling/{siblingType}/for/{personId}")
    @Operation(
            summary = "Add a sibling for a person",
            description = "Creates and assigns a sibling (brother or sister) for a given person, ensuring the family is updated accordingly, and redirects to the sibling's detail page.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Sibling added successfully. Redirected to the sibling's details page.",
                            headers = @Header(name = "Location",
                                    description = "URL of the sibling's details page",
                                    schema = @Schema(type = "string", example = "/person/{id}"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid sibling type.",
                            content = @Content(mediaType = "text/plain", examples = {
                                    @ExampleObject(value = "Invalid sibling type")
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Person not found.",
                            content = @Content(mediaType = "text/plain", examples = {
                                    @ExampleObject(value = "Person not found")
                            })
                    )
            }
    )
    public ResponseEntity<String> addSibling(@PathVariable UUID personId, @PathVariable String siblingType) {
        try {
            SexEnum siblingSex = determineSiblingSex(siblingType);
            Person savedSibling = createSibling(personId, siblingSex);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/person/" + savedSibling.getId())
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid sibling type");
        } catch (PersonNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found");
        }
    }

    private SexEnum determineSiblingSex(String siblingType) {
        return switch (siblingType.toLowerCase()) {
            case "sister" -> SexEnum.FEMALE;
            case "brother" -> SexEnum.MALE;
            default -> throw new IllegalArgumentException("Invalid sibling type");
        };
    }

    private Person createSibling(UUID personId, SexEnum siblingSex) throws PersonNotFoundException {
        Person person = personService.getById(personId);

        PersonDto sibling = new PersonDto();
        sibling.setSex(siblingSex);

        Person savedSibling = personService.saveSibling(sibling, person);
        familyService.saveChild(person.getFamily(), savedSibling);

        return savedSibling;
    }

    @GetMapping("/add/child/{childType}/for/{firstParentId}/{secondParentId}")
    @Operation(
            summary = "Add a child for two parents",
            description = "Creates and assigns a child (son or daughter) for the specified parents, ensuring the family is updated accordingly, and redirects to the child's detail page.",
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "Child added successfully. Redirected to the child's details page.",
                            headers = @Header(name = "Location", description = "URL of the child's details page", schema = @Schema(type = "string", example = "/person/{id}"))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid child type or parent configuration.",
                            content = @Content(mediaType = "text/plain", examples = {
                                    @ExampleObject(value = "Invalid child type"),
                                    @ExampleObject(value = "A child cannot exist without parents")
                            })
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Parent not found.",
                            content = @Content(mediaType = "text/plain", examples = {
                                    @ExampleObject(value = "Person not found")
                            })
                    )
            }
    )
    public ResponseEntity<String> addChild(@PathVariable UUID firstParentId, @PathVariable UUID secondParentId, @PathVariable String childType) {
        try {
            SexEnum childSex = determineChildSex(childType);
            Person firstPerson = personService.getById(firstParentId);
            Person secondPerson = personService.getById(secondParentId);
            Person savedChild = createChild(firstPerson, secondPerson, childSex);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/person/" + savedChild.getId())
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid child type or parents");
        } catch (PersonNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Person not found");
        }
    }

    private SexEnum determineChildSex(String childType) {
        return switch (childType.toLowerCase()) {
            case "daughter" -> SexEnum.FEMALE;
            case "son" -> SexEnum.MALE;
            default -> throw new IllegalArgumentException("Invalid sibling type");
        };
    }

    private Person createChild(Person firstParent, Person secondParent, SexEnum childSex) {
        if (firstParent == null || secondParent == null) {
            throw new IllegalArgumentException("A child cannot exist without parents");
        }

        PersonDto child = new PersonDto();
        child.setSex(childSex);

        Person savedChild = personService.createChildAndSave(child, firstParent, secondParent);
        familyService.saveChild(firstParent, secondParent, savedChild);

        return savedChild;
    }
}
