/**
 * Copyright (c) 2012-2013 All Rights Reserved.
 */
package com.easy.core.validator;

import javax.servlet.http.HttpServletRequest;

import com.easy.core.validator.annotations.RequiredStringValidator;

/**
 * 
 * @author wy
 * @version v 0.1 2013-9-10 下午11:05:08 wy Exp $
 */
public class RequiredStringFieldValidator extends AbstractFieldValidator<RequiredStringValidator> {

    /**
     * @see com.easy.core.validator.FieldValidator#isValid(java.lang.Object,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ValidatorResult isValid(RequiredStringValidator validator, HttpServletRequest request) {

        String[] values = getParameters(validator.field(), request);

        ValidatorResult failResult = new ValidatorResult(validator.field(), values,
            validator.key(), validator.message());

        // 为空，该字段没提交？
        if (values.length == 0) {
            return failResult;
        }

        for (String value : values) {
            if (value == null || (validator.trim() && value.trim().length() == 0)) {
                return failResult;
            }
        }

        return new ValidatorResult(values);
    }

}
