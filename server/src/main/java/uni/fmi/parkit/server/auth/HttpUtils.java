package uni.fmi.parkit.server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import uni.fmi.parkit.server.controllers.ApiError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Utility class that provides helper functions to work with http
 */
public final class HttpUtils {

    /**
     * Sends error response with the specified status code and error message
     *
     * @param res
     * @param statusCode
     * @param errorMessage
     * @throws IOException
     */
    public static void sendApiError(HttpServletResponse res, int statusCode, String errorMessage) throws IOException {
        ApiError errorResponse = new ApiError(statusCode, errorMessage);
        res.setContentType("application/json");
        res.getWriter().write(convertObjectToString(errorResponse));
        res.setStatus(statusCode);
    }

    private HttpUtils() {
    }

    private static <T> String convertObjectToString(T object) {
        return convertData(object, mapper -> mapper.writeValueAsString(object));
    }

    private static <T, V> V convertData(T data, CheckedFunction<ObjectMapper, V> dataConverter) {
        if (data == null) {
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return dataConverter.apply(mapper);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FunctionalInterface
    private interface CheckedFunction<T, R> {
        R apply(T t) throws IOException;
    }
}
