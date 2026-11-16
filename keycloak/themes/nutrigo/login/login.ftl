<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=(realm.password && realm.registrationAllowed && !registrationDisabled??); section>
    <#if section = "header">
        Bienvenido de nuevo
    <#elseif section = "form">
        <p class="ng-subtitle">Ingresa a tu cuenta para continuar con tu seguimiento nutricional.</p>

        <#if realm.password>
            <form id="kc-form-login" class="ng-form" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post" novalidate>
                <#if !usernameHidden??>
                    <div class="ng-field">
                        <label for="username" class="ng-label">
                            <#if !realm.loginWithEmailAllowed>Usuario<#elseif !realm.registrationEmailAsUsername>Usuario o correo electrónico<#else>Correo electrónico</#if>
                        </label>
                        <div class="ng-input-wrap">
                            <span class="ng-input-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
                            </span>
                            <input id="username" name="username" type="text" class="ng-input"
                                   value="${(login.username!'')}" placeholder="ejemplo@correo.com"
                                   autofocus autocomplete="username"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true<#else>false</#if>">
                        </div>
                        <#if messagesPerField.existsError('username','password')>
                            <span id="input-error" class="ng-field-error" aria-live="polite">
                                ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                            </span>
                        </#if>
                    </div>
                </#if>

                <div class="ng-field">
                    <label for="password" class="ng-label">Contraseña</label>
                    <div class="ng-input-wrap">
                        <span class="ng-input-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
                        </span>
                        <input id="password" name="password" type="password" class="ng-input ng-input-eye"
                               placeholder="••••••••" autocomplete="current-password"
                               <#if usernameHidden??>autofocus</#if>
                               aria-invalid="<#if messagesPerField.existsError('username','password')>true<#else>false</#if>">
                        <button type="button" class="ng-eye" data-toggle-password="password" aria-label="Ver u ocultar contraseña">
                            <svg class="ng-eye-show" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                            <svg class="ng-eye-hide" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18"/></svg>
                        </button>
                    </div>
                    <#if usernameHidden?? && messagesPerField.existsError('username','password')>
                        <span id="input-error" class="ng-field-error" aria-live="polite">
                            ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                        </span>
                    </#if>
                </div>

                <div class="ng-options">
                    <#if realm.rememberMe && !usernameHidden??>
                        <label class="ng-check">
                            <input id="rememberMe" name="rememberMe" type="checkbox" class="ng-checkbox" <#if login.rememberMe??>checked</#if>>
                            <span>Recordarme</span>
                        </label>
                    <#else>
                        <span></span>
                    </#if>
                    <#if realm.resetPasswordAllowed>
                        <a class="ng-link" href="${url.loginResetCredentialsUrl}">¿Olvidaste tu contraseña?</a>
                    </#if>
                </div>

                <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>>
                <button class="ng-btn ng-btn-primary ng-btn-block" name="login" id="kc-login" type="submit">Iniciar sesión &gt;</button>
            </form>
        </#if>
    <#elseif section = "info">
        ¿Aún no tienes una cuenta?
        <a class="ng-link" href="${url.registrationUrl}">Regístrate aquí</a>
    <#elseif section = "socialProviders">
        <#if realm.password && social?? && social.providers?has_content>
            <div class="ng-social-login">
                <p class="ng-divider"><span>o continúa con</span></p>
                <#list social.providers as p>
                    <a id="social-${p.alias}" class="ng-btn ng-btn-secondary ng-btn-block" href="${p.loginUrl}">${p.displayName!}</a>
                </#list>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>
