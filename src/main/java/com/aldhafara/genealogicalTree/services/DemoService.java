package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.entities.Family;
import com.aldhafara.genealogicalTree.services.interfaces.FamilyService;
import com.aldhafara.genealogicalTree.services.interfaces.PersonService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DemoService {

    private final PersonService personService;
    private final FamilyService familyService;

    public DemoService(PersonService personService, FamilyService familyService) {
        this.personService = personService;
        this.familyService = familyService;
    }

    public UUID loadDuckTalesDemo() {
        Optional<UUID> existingHumperdinkDuck = personService.findIdByFirstNameAndLastName("Abraham", "Kaczor");

        if (existingHumperdinkDuck.isPresent()) {
            return existingHumperdinkDuck.get();
        }
        UUID mockAddByUserId = UUID.randomUUID();
        Person sylasEliasMcDuck = createAndSavePerson("Sylas Eliasz", "McKwacz", mockAddByUserId);

        Person feliciaCoot = createAndSavePerson("Felicja", "Kuper", mockAddByUserId);
        Family dingusMcDuckFamily = Family.builder().mother(feliciaCoot).father(sylasEliasMcDuck).addBy(mockAddByUserId).build();

        Person dingusMcDuck = createAndSavePerson("Brudus", "McKwacz", mockAddByUserId);
        dingusMcDuckFamily.addChild(dingusMcDuck);
        familyService.save(dingusMcDuckFamily);

        Person mollyMallard = createAndSavePerson("Pullarda", "Drób", mockAddByUserId);
        Family fergusMcDuckFamily = Family.builder().mother(mollyMallard).father(dingusMcDuck).addBy(mockAddByUserId).build();

        Person fergusMcDuck = createAndSavePerson("Fergus", "McKwacz", mockAddByUserId);
        fergusMcDuckFamily.addChild(fergusMcDuck);
        familyService.save(fergusMcDuckFamily);

        Person downyODrake = createAndSavePerson("Kaczencja", "O’Draka", mockAddByUserId);

        Family gideonMcDuckFamily = Family.builder().mother(downyODrake).father(fergusMcDuck).addBy(mockAddByUserId).build();

        Person gideonMcDuck = createAndSavePerson("Gideon", "McKwacz", mockAddByUserId);
        gideonMcDuckFamily.addChild(gideonMcDuck);

        Person matildaMcDuck = createAndSavePerson("Matylda", "McKwacz", mockAddByUserId);
        gideonMcDuckFamily.addChild(matildaMcDuck);

        Person gladiusMcDuck = createAndSavePerson("Gladiusz", "Kwaczyński", mockAddByUserId);
        Family gladiusMcDuckFamily = Family.builder().mother(matildaMcDuck).father(gladiusMcDuck).addBy(mockAddByUserId).build();
        familyService.save(gladiusMcDuckFamily);

        Person scroogeMcDuck = createAndSavePerson("Sknerus", "McKwacz", mockAddByUserId);
        gideonMcDuckFamily.addChild(scroogeMcDuck);

        Person hortenseMcDuck = createAndSavePerson("Hortensja", "McKwacz", mockAddByUserId);
        gideonMcDuckFamily.addChild(hortenseMcDuck);
        familyService.save(gideonMcDuckFamily);

        Person corneliusCoot = createAndSavePerson("Korneliusz", "Kwaczak", mockAddByUserId);

        Person plukahontas = createAndSavePerson("Plukahontas", "", mockAddByUserId);
        Family clintonCootFamily = Family.builder().mother(plukahontas).father(corneliusCoot).addBy(mockAddByUserId).build();

        Person clintonCoot = createAndSavePerson("Kwalutek", "Kwaczak", mockAddByUserId);
        clintonCootFamily.addChild(clintonCoot);
        familyService.save(clintonCootFamily);

        Person gertrudeGadwall = createAndSavePerson("Gertruda", "Perliczek", mockAddByUserId);
        Family elviraCootFamily = Family.builder().mother(gertrudeGadwall).father(clintonCoot).addBy(mockAddByUserId).build();

        Person elviraCoot = createAndSavePerson("Anetta", "Kwaczak", mockAddByUserId);
        elviraCootFamily.addChild(elviraCoot);
        familyService.save(elviraCootFamily);

        Person humperdinkDuck = createAndSavePerson("Abraham", "Kaczor", mockAddByUserId);
        Family quackmoreDuckFamily = Family.builder().mother(elviraCoot).father(humperdinkDuck).addBy(mockAddByUserId).build();

        Person eiderDuck = createAndSavePerson("Eider", "Kaczor", mockAddByUserId);
        quackmoreDuckFamily.addChild(eiderDuck);

        Person lulubelleLoon = createAndSavePerson("Dzióbella", "Kaczka", mockAddByUserId);
        Family fethryDuckFamily = Family.builder().mother(lulubelleLoon).father(eiderDuck).addBy(mockAddByUserId).build();

        Person fethryDuck = createAndSavePerson("Dziobas", "Kaczor", mockAddByUserId);
        fethryDuckFamily.addChild(fethryDuck);

        Person abnerWhitewaterDuck = createAndSavePerson("Fiksat", "Kaczor", mockAddByUserId);
        fethryDuckFamily.addChild(abnerWhitewaterDuck);
        familyService.save(fethryDuckFamily);

        Person daphneDuck = createAndSavePerson("Dafnia", "Kaczka", mockAddByUserId);
        quackmoreDuckFamily.addChild(daphneDuck);

        Person goostaveGander = createAndSavePerson("Kwacław", "Kwabotyn", mockAddByUserId);
        Family gladstoneGanderFamily = Family.builder().mother(daphneDuck).father(goostaveGander).addBy(mockAddByUserId).build();

        Person gladstoneGander = createAndSavePerson("Goguś", "Kwabotyn", mockAddByUserId);
        gladstoneGanderFamily.addChild(gladstoneGander);
        familyService.save(gladstoneGanderFamily);

        Person quackmoreDuck = createAndSavePerson("Kwaczymon", "Kaczor", mockAddByUserId);
        quackmoreDuckFamily.addChild(quackmoreDuck);
        familyService.save(quackmoreDuckFamily);

        Family donaldDuckFamily = Family.builder().mother(hortenseMcDuck).father(quackmoreDuck).addBy(mockAddByUserId).build();

        Person dellaDuck = createAndSavePerson("Della", "Kaczka", mockAddByUserId);
        donaldDuckFamily.addChild(dellaDuck);

        Person donaldDuck = createAndSavePerson("Donald", "Kaczor", mockAddByUserId);
        donaldDuckFamily.addChild(donaldDuck);
        familyService.save(donaldDuckFamily);

        Person unknownDuck = createAndSavePerson("?", "Kaczor", mockAddByUserId);
        Family nephewsFamily = Family.builder().mother(dellaDuck).father(unknownDuck).addBy(mockAddByUserId).build();

        Person hueyDuck = createAndSavePerson("Hyzio", "Kaczor", mockAddByUserId);
        nephewsFamily.addChild(hueyDuck);

        Person deweyDuck = createAndSavePerson("Dyzio", "Kaczor", mockAddByUserId);
        nephewsFamily.addChild(deweyDuck);

        Person louieDuck = createAndSavePerson("Zyzio", "Kaczor", mockAddByUserId);
        nephewsFamily.addChild(louieDuck);
        familyService.save(nephewsFamily);

        Person kwakierKwaczak = createAndSavePerson("Kwakier", "Kwaczak", mockAddByUserId);

        Person gretaGrabo = createAndSavePerson("Greta", "Grabo", mockAddByUserId);
        Family knotheadDuckFamily = Family.builder().mother(gretaGrabo).father(kwakierKwaczak).addBy(mockAddByUserId).build();

        Person knotheadDuck = createAndSavePerson("Koguton", "Kwaczak", mockAddByUserId);
        knotheadDuckFamily.addChild(knotheadDuck);

        Person melindaDucksworth = createAndSavePerson("Balbina", "Kwaczak", mockAddByUserId);
        knotheadDuckFamily.addChild(melindaDucksworth);
        familyService.save(knotheadDuckFamily);

        Person balbinaQuack = createAndSavePerson("Balbina", "Kwaczak", mockAddByUserId);

        Person luciusGeng = createAndSavePerson("Lucjusz", "Gęg", mockAddByUserId);
        Family gusGooseFamily = Family.builder().mother(balbinaQuack).father(luciusGeng).addBy(mockAddByUserId).build();

        Person gusGoose = createAndSavePerson("Gęgul", "Gęg", mockAddByUserId);
        gusGooseFamily.addChild(gusGoose);
        familyService.save(gusGooseFamily);

        return humperdinkDuck.getId();
    }

    private Person createAndSavePerson(String firstName, String lastName, UUID mockAddByUserId) {
        Person person = Person.builder().firstName(firstName).lastName(lastName).addBy(mockAddByUserId).build();
        personService.save(person);
        return person;
    }
}
