package com.software.codetime.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.io.IOUtils;
import swdc.java.ops.manager.EventTrackerManager;
import swdc.java.ops.snowplow.entities.UIElementEntity;
import swdc.java.ops.snowplow.events.UIInteractionType;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ReadmeManager {

    public static void openReadmeFile(UIInteractionType interactionType) {
        ApplicationManager.getApplication().invokeLater(() -> {
            Project p = IntellijProjectManager.getOpenProject();
            if (p == null) {
                return;
            }

            UIElementEntity elementEntity = new UIElementEntity();
            elementEntity.element_name = interactionType == UIInteractionType.click ? "ct_learn_more_btn" : "ct_learn_more_cmd";
            elementEntity.element_location = interactionType == UIInteractionType.click ? "ct_menu_tree" : "ct_command_palette";
            elementEntity.color = interactionType == UIInteractionType.click ? "yellow" : null;
            elementEntity.cta_text = "Learn more";
            elementEntity.icon_name = interactionType == UIInteractionType.click ? "document" : null;
            EventTrackerManager.getInstance().trackUIInteraction(interactionType, elementEntity);

            ClassLoader classLoader = ReadmeManager.class.getClassLoader();
            String readmeFile = UserSessionManager.getReadmeFile();
            try (InputStream inputStream = classLoader.getResourceAsStream("assets/README.md")) {

                String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                File f = new File(readmeFile);
                if (!f.exists()) {
                    Writer writer = null;
                    // write the summary content
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(readmeFile), StandardCharsets.UTF_8));
                        writer.write(fileContent);
                    } catch (IOException ex) {
                        // Report
                    } finally {
                        try {
                            writer.close();
                        } catch (Exception ex) {/*ignore*/}
                    }
                }
                StatusBarManager.launchFile(readmeFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
