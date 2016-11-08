package se.curity.oauth.core.request;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.state.SslState;
import se.curity.oauth.core.util.MapBuilder;
import se.curity.oauth.core.util.UnsafeSSLContextProvider;
import se.curity.oauth.core.util.event.Event;

import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Simplified representation of a HTTP Request that is enough to represent any OAuth request.
 */
public abstract class HttpRequest implements Event
{

    private final String method;
    private final String baseUrl;
    private final String path;
    private final Map<String, Object> headers;
    private final MultivaluedMap<String, String> query;

    @Nullable
    private final Entity<?> _entity;

    private final Function<Object, String> urlEncoder = obj ->
    {
        try
        {
            return URLEncoder.encode(obj.toString().trim(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("This runtime does not support UTF-8. Cannot run the application.");
        }
    };

    HttpRequest(String method,
                String baseUrl,
                String path,
                Map<String, Object> headers,
                MultivaluedMap<String, String> query)
    {
        this(method, baseUrl, path, headers, query, null);
    }

    HttpRequest(String method,
                String baseUrl,
                String path,
                Map<String, Object> headers,
                MultivaluedMap<String, String> query,
                @Nullable Entity<?> entity)
    {
        this.method = method;
        this.baseUrl = baseUrl;
        this.path = path;
        this.headers = Collections.unmodifiableMap(headers);
        this.query = new ImmutableMultivaluedMap<>(query);
        _entity = entity;
    }

    public Response send(@Nullable SslState sslState)
    {
        Client client = createClientWith(sslState).build();

        WebTarget target = client.target(baseUrl).path(path);

        for (Map.Entry<String, List<String>> parameterEntry : query.entrySet())
        {
            for (String value : parameterEntry.getValue())
            {
                target = target.queryParam(parameterEntry.getKey(), value);
            }
        }

        System.out.println("Request target: " + target);

        Invocation.Builder requestBuilder = target.request();

        headers.forEach(requestBuilder::header);

        return (_entity == null) ?
                requestBuilder.method(method) :
                requestBuilder.method(method, _entity);
    }

    protected ClientBuilder createClientWith(@Nullable SslState sslState)
    {
        if (sslState == null)
        {
            return ClientBuilder.newBuilder();
        }

        SSLContext sslContext;

        if (sslState.isIgnoreSSL())
        {
            sslContext = UnsafeSSLContextProvider.getInstance().get();
        }
        else
        {
            sslContext = SslConfigurator.newInstance()
                    .trustStoreFile(sslState.getTrustStoreFile())
                    .trustStorePassword(sslState.getTrustStorePassword())
                    .keyStoreFile(sslState.getKeystoreFile())
                    .keyPassword(sslState.getKeystorePassword())
                    .createSSLContext();
        }

        return ClientBuilder.newBuilder().sslContext(sslContext);
    }

    public String toCurl(@Nullable SslState sslState, @Nullable GeneralState generalState)
    {
        List<String> commandParts = new ArrayList<>();

        String curlHeaders = String.join(" ", headers.entrySet().stream()
                .map(it -> "-H \"" + it.getKey() + ": " + it.getValue() + "\"")
                .collect(Collectors.toList()));

        String sslOption = (sslState != null && sslState.isIgnoreSSL()) ? "-k" : "";
        String verboseOption = (generalState != null && generalState.isVerbose()) ? "-v" : "";

        String queryString = query.isEmpty() ? "" :
                "?" + String.join("&", query.entrySet().stream()
                        .flatMap(it -> it.getValue().stream()
                                .map(v -> urlEncoder.apply(it.getKey()) + "=" + urlEncoder.apply(v)))
                        .collect(Collectors.toList()));

        String absolutePath = path.startsWith("/") ? path : "/" + path;
        String fullUrl = "\"" + baseUrl + absolutePath + queryString + "\"";

        commandParts.add("curl -X");
        commandParts.add(method);

        if (!sslOption.isEmpty())
        {
            commandParts.add(sslOption);
        }

        if (!verboseOption.isEmpty())
        {
            commandParts.add(verboseOption);
        }

        commandParts.add(curlHeaders);
        commandParts.add(fullUrl);

        return String.join(" ", commandParts);
    }

    static MapBuilder<String, Object> oauthBasicHeaders()
    {
        return new MapBuilder<String, Object>()
                .put("Accept", "application/json");
    }

    public static MultivaluedMap<String, String> parseQueryParameters(@Nullable String query)
    {
        MultivaluedMap<String, String> queryParameters = new MultivaluedHashMap<>();

        if (query == null || query.isEmpty())
        {
            return queryParameters;
        }

        for (String part : query.split("&"))
        {
            int equalsIndex = part.indexOf('=');
            if (equalsIndex < 0)
            {
                queryParameters.putSingle(part, "");
            }
            else
            {
                String key = part.substring(0, equalsIndex);
                String value = part.substring(equalsIndex);
                queryParameters.putSingle(key, value);
            }
        }

        return queryParameters;
    }

    @Override
    public String toString()
    {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", query=" + query +
                ", entity='" + _entity + '\'' +
                '}';
    }
}
