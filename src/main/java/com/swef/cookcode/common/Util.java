package com.swef.cookcode.common;

import com.swef.cookcode.common.error.exception.InvalidRequestException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
    public static <T> void validateDuplication(List<T> list1, List<T> list2) {
        Set<T> mergedSets = new HashSet<>() {{
            addAll(list1);
            addAll(list2);
        }};
        if (mergedSets.size() < list1.size() + list2.size()) {
            throw new InvalidRequestException(ErrorCode.DUPLICATED);
        }
    }
}
