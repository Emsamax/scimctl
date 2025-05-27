import com.fasterxml.jackson.databind.JsonNode;
import com.worldline.mts.idm.scimctl.commands.import_cmd.ResourceStreamBuilder;
import de.captaingoldfish.scim.sdk.common.resources.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Inherited;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class StreamBuilderTest {



  private ResourceStreamBuilder resourceStreamBuilder;

  private Iterator<List<JsonNode>> iterator;

  private static String PATH = "src/test/resources/users.csv";
 /*
  private static String HEADER = "userName,name/familyName,name/givenName,name/honorificPrefix,displayName,emails/0/value,emails/0/type,emails/0/primary,phoneNumbers/0/value,phoneNumbers/0/type,active,roles/0/value,roles/0/display,emails/1/value,emails/1/type,roles/1/value,roles/1/display";
  private static String JDOE = "jdoe,Doe,John,M.,John Doe,jdoe@example.com,work,true,+12345678901,mobile,true,user,User,,,,";
  private static String ASMITH = "asmith,Smith,Alice,Ms.,Alice Smith,asmith@example.com,work,true,+12345678902,mobile,true,admin,Administrator,,,,";
  private static String RJOHNSON = "rjohnson,Johnson,Robert,Mr.,Robert Johnson,rjohnson@example.com,work,true,+12345678903,home,true,user,User,rjohnson.personal@example.com,home,manager,Manager\n";
  private static String LJONES = "ljones,Jones,Laura,Ms.,Laura Jones,ljones@example.com,work,,+12345678904,mobile,false,user,User,,,,";
  private static String MWHITE = "mwhite,White,Michael,Dr.,Dr. Michael White,mwhite@example.com,work,true,+12345678905,work,true,user,User,,,,";
  */

  @BeforeAll
  public static void setUp() throws IOException {


  }

  @Test
  void test_parse_from_csv_file() {
    /*try {
      resourceStreamBuilder = new ResourceStreamBuilder();
      iterator = resourceStreamBuilder.fromFile(new File(PATH)).build().convert().chunk(50).iterator();
      var chunk = iterator.next();
      var listIterator = chunk.listIterator();
      User streamJdoe = (User) listIterator.next();

      assertNotNull(streamJdoe);
      System.out.println("TEST+++++++++++");
      assertEquals("jdoe", streamJdoe.getUserName());
      assertEquals("Doe", streamJdoe.get("familyName"));
      assertEquals("John", streamJdoe.get("givenName"));
      assertEquals("M.", streamJdoe.get("honorificPrefix"));
      assertEquals("John Doe", streamJdoe.getDisplayName());
      assertNotNull(streamJdoe.getEmails());
      assertEquals(1, streamJdoe.getEmails().size());
      assertEquals("jdoe@example.com", streamJdoe.getEmails().get(0).getValue());
      assertEquals("work", streamJdoe.getEmails().get(0).getType());
      assertTrue(streamJdoe.getEmails().get(0).isPrimary());

    } catch (IOException e){
      System.err.println(e.getMessage());
    }
*/
  }

}

