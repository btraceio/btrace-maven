package io.btrace.mvnplugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

public final class AgentPropertiesTransformer implements ResourceTransformer {
    private static final String AGENT_PROPS_FILE = "META-INF/btrace/agent.properties";
    private static final String SCRIPT_ARG_NAME = "script";
    private static final String SCRIPT_DELIMITER = ":";

    private Properties props;

    @Override
    public boolean canTransformResource(String rsrc) {
        return AGENT_PROPS_FILE.equals(rsrc);
    }

    @Override
    public void processResource(String rsrc, InputStream in, List<Relocator> relocators) throws IOException {
        Properties rsrcProps = new Properties();
        rsrcProps.load(in);

        if (props == null) {
            props = rsrcProps;
        } else {
            for (Map.Entry<Object, Object> e : rsrcProps.entrySet()) {
                if (SCRIPT_ARG_NAME.equals(e.getKey())) {
                    String current = props.getProperty(SCRIPT_ARG_NAME);
                    if (current == null) {
                        props.put(e.getKey(), e.getValue());
                    } else {
                        props.put(e.getKey(), current + SCRIPT_DELIMITER + e.getValue());
                    }
                } else {
                    props.put(e.getKey(), e.getValue());
                }
            }
        }
        in.close();
    }

    @Override
    public boolean hasTransformedResource() {
        return props != null;
    }

    @Override
    public void modifyOutputStream(JarOutputStream os) throws IOException {
        os.putNextEntry(new JarEntry(AGENT_PROPS_FILE));
    }
}
