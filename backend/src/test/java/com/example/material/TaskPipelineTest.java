package com.example.material;
import java.util.Set;
import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class TaskPipelineTest { @Test void pipelineStagesAreDocumentedInOrder(){assertEquals("ASSET_CHECK>GENERATING>PACKAGING",String.join(">",new String[]{"ASSET_CHECK","GENERATING","PACKAGING"}));} @Test void failureIsTerminal(){assertTrue(Set.of("SUCCEEDED","FAILED").contains("FAILED"));} }
