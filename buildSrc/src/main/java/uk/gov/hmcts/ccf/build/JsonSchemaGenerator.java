package uk.gov.hmcts.ccf.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.imifou.jsonschema.module.addon.AddonModule;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.google.common.reflect.ClassPath;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class JsonSchemaGenerator extends DefaultTask {

    @Inject
    public JsonSchemaGenerator() {
        dependsOn(getProject().getTasks().getByName("compileJava"));
    }

    @TaskAction
    public void run() throws IOException {
        Project project = getProject();
        JavaPluginConvention plugin = getProject().getConvention().getPlugin(JavaPluginConvention.class);
        List<URL> urls = plugin.getSourceSets().getByName("main").getRuntimeClasspath().getFiles()
                .stream().map(x -> {
                    try {
                        return x.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());

        ClassLoader ours = Thread.currentThread().getContextClassLoader();
        URLClassLoader classloader = new URLClassLoader(urls.toArray(new URL[0]), ours);
        File schemaFolder = project.getRootProject().file("angular/src/assets/schema");
        schemaFolder.mkdir();
        Map<String, JsonNode> schema = new HashMap<>();
        for (ClassPath.ClassInfo topLevelClass : ClassPath.from(classloader).getTopLevelClasses("uk.gov.hmcts.unspec.event")) {
            JacksonModule module = new JacksonModule();
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09,
                    OptionPreset.PLAIN_JSON).with(new AddonModule()).with(module);
            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);
            JsonNode jsonSchema = generator.generateSchema(topLevelClass.load());
            schema.put(topLevelClass.getSimpleName(), jsonSchema);
        }

        for (ClassPath.ClassInfo topLevelClass : ClassPath.from(classloader).getTopLevelClasses("uk.gov.hmcts.unspec.dto")) {
            JacksonModule module = new JacksonModule();
            SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09,
                    OptionPreset.PLAIN_JSON).with(new AddonModule()).with(module);
            SchemaGeneratorConfig config = configBuilder.build();
            SchemaGenerator generator = new SchemaGenerator(config);
            JsonNode jsonSchema = generator.generateSchema(topLevelClass.load());
            schema.put(topLevelClass.getSimpleName(), jsonSchema);
        }

        File dest = new File(schemaFolder, "schema.json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
            writer.write(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(schema));
        }
    }

}
