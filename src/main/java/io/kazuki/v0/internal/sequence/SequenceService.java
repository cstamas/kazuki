package io.kazuki.v0.internal.sequence;

public interface SequenceService {
    public String getTypeName(final Integer id) throws Exception;

    public Integer getTypeId(final String type, final boolean create)
            throws Exception;

    public Integer getTypeId(final String type, final boolean create,
            final boolean strict) throws Exception;
}