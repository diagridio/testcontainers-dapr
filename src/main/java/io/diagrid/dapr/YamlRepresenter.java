package io.diagrid.dapr;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

class YamlRepresenter extends Representer {
    public YamlRepresenter(DumperOptions options) {
        super(options);
        this.representers.put(QuotedBoolean.class, new RepresentQuotedBoolean());
    }

    private class RepresentQuotedBoolean implements Represent {
        public Node representData(Object data) {
            QuotedBoolean str = (QuotedBoolean) data;
            return representScalar(
                    Tag.STR, str.getValue(), DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        }
    }
}
