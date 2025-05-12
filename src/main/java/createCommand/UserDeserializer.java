package createCommand;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.captaingoldfish.scim.sdk.common.resources.User;
import com.fasterxml.jackson.core.JsonParser;
import de.captaingoldfish.scim.sdk.common.resources.complex.Name;
import de.captaingoldfish.scim.sdk.common.resources.multicomplex.Address;
import de.captaingoldfish.scim.sdk.common.resources.multicomplex.Email;
import de.captaingoldfish.scim.sdk.common.resources.multicomplex.PhoneNumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer() {
        this(null);
    }

    public UserDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public User deserialize(JsonParser parser, DeserializationContext context) {
        User user = null;
        try {
            ObjectMapper mapper = (ObjectMapper) parser.getCodec();
            JsonNode node = mapper.readTree(parser);
            User.UserBuilder userBuilder = new User.UserBuilder();
            //user entity
            Optional.ofNullable(node.get("userName")).map(JsonNode::asText).ifPresent(userBuilder::userName);
            Optional.ofNullable(node.get("displayName")).map(JsonNode::asText).ifPresent(userBuilder::displayName);
            Optional.ofNullable(node.get("nickName")).map(JsonNode::asText).ifPresent(userBuilder::nickName);
            Optional.ofNullable(node.get("profileUrl")).map(JsonNode::asText).ifPresent(userBuilder::profileUrl);
            Optional.ofNullable(node.get("title")).map(JsonNode::asText).ifPresent(userBuilder::title);
            Optional.ofNullable(node.get("userType")).map(JsonNode::asText).ifPresent(userBuilder::userType);
            Optional.ofNullable(node.get("preferredLanguage")).map(JsonNode::asText).ifPresent(userBuilder::preferredLanguage);
            Optional.ofNullable(node.get("locale")).map(JsonNode::asText).ifPresent(userBuilder::locale);
            Optional.ofNullable(node.get("timeZone")).map(JsonNode::asText).ifPresent(userBuilder::timeZone);
            Optional.ofNullable(node.get("active")).map(JsonNode::asBoolean).ifPresent(userBuilder::active);
            Optional.ofNullable(node.get("password")).map(JsonNode::asText).ifPresent(userBuilder::password);

            user = userBuilder
                    .name(parseName(node))
                    .addresses(parseAdresses(node))
                    .emails(parseEmails(node))
                    .phoneNumbers(parsePhoneNumber(node))
                    .build();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return user;
    }

    private ArrayList<Address> parseAdresses(JsonNode node) throws IOException {
        //address entity
        Address.AddressBuilder addressBuilder = new Address.AddressBuilder();
        var addressList = new ArrayList<Address>();
        Optional.ofNullable(node).flatMap(n -> Optional.ofNullable(n.get("addresses")))
                .ifPresent(addressNode -> {
                    Optional.ofNullable(addressNode.get("type")).map(JsonNode::asText).ifPresent(addressBuilder::type);
                    Optional.ofNullable(addressNode.get("street_adress")).map(JsonNode::asText).ifPresent(addressBuilder::streetAddress);
                    Optional.ofNullable(addressNode.get("locality")).map(JsonNode::asText).ifPresent(addressBuilder::locality);
                    Optional.ofNullable(addressNode.get("region")).map(JsonNode::asText).ifPresent(addressBuilder::region);
                    Optional.ofNullable(addressNode.get("postal_code")).map(JsonNode::asText).ifPresent(addressBuilder::postalCode);
                    Optional.ofNullable(addressNode.get("country")).map(JsonNode::asText).ifPresent(addressBuilder::country);
                    addressList.add(addressBuilder.build());
                });
        return addressList;
    }

    private Name parseName(JsonNode node) throws IOException {
        //name entity
        Name.NameBuilder nameBuilder = new Name.NameBuilder();
        Optional.ofNullable(node).flatMap(n -> Optional.ofNullable(n.get("name")))
                .ifPresent(nameNode -> {
                    Optional.ofNullable(nameNode.get("familyName")).map(JsonNode::asText).ifPresent(nameBuilder::familyName);
                    Optional.ofNullable(nameNode.get("givenName")).map(JsonNode::asText).ifPresent(nameBuilder::givenName);
                    Optional.ofNullable(nameNode.get("middleName")).map(JsonNode::asText).ifPresent(nameBuilder::middlename);
                    Optional.ofNullable(nameNode.get("honorificPrefix")).map(JsonNode::asText).ifPresent(nameBuilder::honorificPrefix);
                    Optional.ofNullable(nameNode.get("honorificSuffix")).map(JsonNode::asText).ifPresent(nameBuilder::honorificSuffix);
                });
        return nameBuilder.build();
    }

    private ArrayList<Email> parseEmails(JsonNode node) throws IOException {
        //email entity
        Email.EmailBuilder emailBuilder = new Email.EmailBuilder();
        var emailsList = new ArrayList<Email>();
        Optional.ofNullable(node).flatMap(n -> Optional.ofNullable(n.get("emails")))
                .ifPresent(emailNode -> {
                    Optional.ofNullable(emailNode.get("value")).map(JsonNode::asText).ifPresent(emailBuilder::value);
                    Optional.ofNullable(emailNode.get("type")).map(JsonNode::asText).ifPresent(emailBuilder::type);
                    Optional.ofNullable(emailNode.get("primary")).map(JsonNode::asBoolean).ifPresent(emailBuilder::primary);
                    emailsList.add(emailBuilder.build());
                });
        return emailsList;
    }

    private ArrayList<PhoneNumber> parsePhoneNumber(JsonNode node) throws IOException {
        //phone number entity
        PhoneNumber.PhoneNumberBuilder phoneNumberBuilder = new PhoneNumber.PhoneNumberBuilder();
        var phoneNumbersList = new ArrayList<PhoneNumber>();
        Optional.ofNullable(node).flatMap(n -> Optional.ofNullable(n.get("phoneNumbers")))
                .ifPresent(phoneNode -> {
                    Optional.ofNullable(phoneNode.get("type")).map(JsonNode::asText).ifPresent(phoneNumberBuilder::type);
                    Optional.ofNullable(phoneNode.get("value")).map(JsonNode::asText).ifPresent(phoneNumberBuilder::value);
                    phoneNumbersList.add(phoneNumberBuilder.build());
                });
        return phoneNumbersList;
    }
}
