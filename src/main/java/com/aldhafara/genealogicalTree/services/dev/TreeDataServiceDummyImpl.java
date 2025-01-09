package com.aldhafara.genealogicalTree.services.dev;

import com.aldhafara.genealogicalTree.exceptions.TreeStructureNotFoundException;
import com.aldhafara.genealogicalTree.services.interfaces.TreeDataService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("test")
public class TreeDataServiceDummyImpl implements TreeDataService {

    @Override
    public String getTreeStructure(UUID id) {
        if (UUID.fromString("123e4567-e89b-12d3-a456-426614174000").equals(id)) {
            return "{\n" +
                    "    \"id\": \"550e8400-e29b-41d4-a716-446655440000\",\n" +
                    "    \"marriageDate\": \"2000-06-15\",\n" +
                    "    \"status\": \"married\",\n" +
                    "    \"father\": {\n" +
                    "        \"id\": \"123e4567-e89b-12d3-a456-426614174000\",\n" +
                    "        \"name\": \"John Smith\",\n" +
                    "        \"familyName\": \"Smith\",\n" +
                    "        \"otherPartners\": []\n" +
                    "    },\n" +
                    "    \"mother\": {\n" +
                    "        \"id\": \"987f6543-b21c-98e7-c567-456789123456\",\n" +
                    "        \"name\": \"Jane Doe\",\n" +
                    "        \"familyName\": \"Doe\",\n" +
                    "        \"otherPartners\": []\n" +
                    "    },\n" +
                    "    \"children\": [\n" +
                    "        {\n" +
                    "            \"id\": \"d34db33f-0bad-cafe-1234-56789abcdef0\",\n" +
                    "            \"name\": \"Alice Smith\",\n" +
                    "            \"familyName\": \"Smith\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"deadbeef-feed-babe-1234-567890abcdef\",\n" +
                    "            \"name\": \"Bob Smith\",\n" +
                    "            \"familyName\": \"Smith\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"8badf00d-cafe-babe-1234-56789abcdef1\",\n" +
                    "            \"name\": \"Charlie Smith\",\n" +
                    "            \"familyName\": \"Smith\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
        }
        throw new TreeStructureNotFoundException(id.toString());
    }
}
