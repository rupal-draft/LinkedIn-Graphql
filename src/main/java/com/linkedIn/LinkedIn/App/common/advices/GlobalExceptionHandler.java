package com.linkedIn.LinkedIn.App.common.advices;

import com.linkedIn.LinkedIn.App.common.exceptions.BaseException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof BaseException baseEx) {
            ApiError error = new ApiError(
                    LocalDateTime.now(),
                    baseEx.getStatus(),
                    baseEx.getMessage(),
                    env.getExecutionStepInfo().getPath().toString()
            );
            return GraphqlErrorBuilder.newError(env)
                    .message(baseEx.getMessage())
                    .extensions(Map.of(
                            "timestamp", error.getTimestamp().toString(),
                            "status", error.getStatus(),
                            "path", error.getPath()
                    ))
                    .build();
        }
        return GraphqlErrorBuilder.newError(env)
                .message("Internal server error")
                .errorType(ErrorType.INTERNAL_ERROR)
                .build();
    }
}
