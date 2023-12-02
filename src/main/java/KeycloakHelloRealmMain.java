import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@QuarkusMain
public class KeycloakHelloRealmMain implements QuarkusApplication {

    Keycloak keycloak;

    @PostConstruct
    public void initKeycloak() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl("http://keycloak:8080")
                .realm("master")
                .clientId("admin-cli")
                .grantType("password")
                .username("admin")
                .password("admin")
                .build();
    }

    @PreDestroy
    public void closeKeycloak() {
        keycloak.close();
    }

    @Override
    public int run(String... args) throws Exception {
        //Create Realm
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm("myRealm");
        realmRepresentation.setDisplayName("Example Realm");
        realmRepresentation.setEnabled(true);
        keycloak.realms().create(realmRepresentation);

        RealmResource myRealm = keycloak.realm("myRealm");


        //Create Client
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId("myclient");
        clientRepresentation.setName("Example Client");
        clientRepresentation.setEnabled(true);
        clientRepresentation.setRedirectUris(List.of("https://www.keycloak.org/app/*"));
        clientRepresentation.setWebOrigins(List.of("https://www.keycloak.org"));
        myRealm.clients().create(clientRepresentation);

        //Create Password
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue("test");

        //Create User and add password
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("myuser");
        userRepresentation.setEmail("test@example.com");
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(List.of(credentialRepresentation));
        myRealm.users().create(userRepresentation);
        return 0;
    }
}