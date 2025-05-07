package cn.sanyeyun.enums;
/**
 *  数据来源
 * @author  徐亚松
 * 2025/5/7 23:52
 */
public enum SourceType {
    MODRINTH,
    CURSEFORGE;

    public String getValue() {
        return name().toLowerCase();
    }

    public static SourceType fromString(String value) {
        for (SourceType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown SourceType: " + value);
    }
}
