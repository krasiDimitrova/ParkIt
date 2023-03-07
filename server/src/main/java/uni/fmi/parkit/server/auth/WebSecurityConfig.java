package uni.fmi.parkit.server.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uni.fmi.parkit.server.services.UserService;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtTokenFilter jwtTokenFilter;

    private SensorUpdateFilter sensorUpdateFilter;

    private UserService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.signing.key}")
    private String jwtSecret;

    @Autowired
    public void setUserDetailsService(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    public void setJwtTokenFilter(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Autowired
    public void setSensorUpdateFilter(SensorUpdateFilter sensorUpdateFilter) {
        this.sensorUpdateFilter = sensorUpdateFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                /**
                 * session won't be used to store user's state, so we use stateless session
                 */
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                /**
                 * handle an authorized attempts
                 */
                .exceptionHandling().authenticationEntryPoint((req, res, e) ->
                        HttpUtils.sendApiError(res, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                .and()
                /**
                 * Add filters to validate authentication through jwt token
                 */
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), jwtSecret), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(sensorUpdateFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                /**
                 *  All /api requests must be authenticated.
                 */
                .authorizeRequests()
                .antMatchers("/api/**/*")
                .authenticated().anyRequest().permitAll();
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
