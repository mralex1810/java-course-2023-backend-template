package edu.java.scrapper.configuration;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

@Service
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final BucketConfiguration bucketConfiguration;
    private final ProxyManager<String> buckets;

    public RateLimiterInterceptor(
        Cache<String, byte[]> cache,
        BucketConfiguration bucketConfiguraton
    ) {
        this.bucketConfiguration = bucketConfiguraton;
        buckets = new JCacheProxyManager<>(cache);
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler
    ) {
        String ip = request.getRemoteAddr();

        // acquire cheap proxy to the bucket
        Bucket bucket = buckets.builder().build(ip, bucketConfiguration);

        // tryConsume returns false immediately if no tokens available with the bucket
        boolean consumed = bucket.tryConsume(1);
        if (!consumed) {
            // limit is exceeded
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        }
        return consumed;
    }
}
