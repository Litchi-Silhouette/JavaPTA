package pku.abs;

import pascal.taie.language.classes.JField;

public class ConvertToField {
    private final AbstractVarDomain domain;
    private final JField field;

    public ConvertToField(AbstractVarDomain domain, JField field) {
        this.domain = domain;
        this.field = field;
    }

    public Integer convert(int v) {
        return domain.getVarField(v, field);
    }

    @Override
    public String toString() {
        return "." + field.getName();
    }
}
