package bg.tuvarna.sit.wms.entities;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AgentTest {

  private Agent agent;

  @BeforeEach
  public void setUp() {
    agent = new Agent();
  }

  @Test
  public void testAgentInitialization() {
    assertNotNull(agent, "Agent should be instantiated.");
  }
}
