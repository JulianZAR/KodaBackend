<#import "template.ftl" as layout>
<#-- Valores enviados previamente (si el registro falló), leídos del perfil de usuario de Keycloak -->
<#assign vals = {}>
<#if profile?? && profile.attributes??>
    <#list profile.attributes as attribute>
        <#assign vals = vals + {attribute.name: (attribute.value!'')}>
    </#list>
</#if>

<#macro campo name label type placeholder autocomplete icon="">
    <div class="ng-field">
        <label for="${name}" class="ng-label">${label}</label>
        <div class="ng-input-wrap">
            <#if icon?has_content>
                <span class="ng-input-icon">${icon}</span>
            </#if>
            <input id="${name}" name="${name}" type="${type}"
                   class="ng-input<#if !icon?has_content> ng-input-plain</#if><#if type == 'password'> ng-input-eye</#if>"
                   <#if type != 'password'>value="${(vals[name])!''}"</#if>
                   placeholder="${placeholder}" autocomplete="${autocomplete}"
                   aria-invalid="<#if messagesPerField.existsError(name)>true<#else>false</#if>">
            <#if type == 'password'>
                <button type="button" class="ng-eye" data-toggle-password="${name}" aria-label="Ver u ocultar contraseña">
                    <svg class="ng-eye-show" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                    <svg class="ng-eye-hide" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18"/></svg>
                </button>
            </#if>
        </div>
        <#if messagesPerField.existsError(name)>
            <span id="input-error-${name}" class="ng-field-error" aria-live="polite">${kcSanitize(messagesPerField.get(name))?no_esc}</span>
        </#if>
    </div>
</#macro>

<#assign iconoCorreo><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg></#assign>
<#assign iconoCandado><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg></#assign>

<@layout.registrationLayout displayMessage=messagesPerField.exists('global') displayInfo=true; section>
    <#if section = "header">
        Crea tu cuenta
    <#elseif section = "form">
        <p class="ng-subtitle">Regístrate para empezar tu seguimiento nutricional personalizado.</p>

        <form id="kc-register-form" class="ng-form" action="${url.registrationAction}" method="post" novalidate onsubmit="registrar.disabled = true; return true;">
            <div class="ng-grid-2">
                <@campo name="firstName" label="Nombre" type="text" placeholder="Laura" autocomplete="given-name"/>
                <@campo name="lastName" label="Apellido" type="text" placeholder="Gómez" autocomplete="family-name"/>
            </div>

            <#if !realm.registrationEmailAsUsername>
                <@campo name="username" label="Usuario" type="text" placeholder="usuario" autocomplete="username"/>
            </#if>

            <@campo name="email" label="Correo electrónico" type="email" placeholder="ejemplo@correo.com" autocomplete="email" icon=iconoCorreo/>
            <@campo name="password" label="Contraseña" type="password" placeholder="••••••••" autocomplete="new-password" icon=iconoCandado/>
            <@campo name="password-confirm" label="Confirmar contraseña" type="password" placeholder="••••••••" autocomplete="new-password" icon=iconoCandado/>

            <#if recaptchaRequired?? && (recaptchaVisible!false)>
                <div class="ng-field">
                    <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}" data-action="${recaptchaAction}"></div>
                </div>
            </#if>

            <button class="ng-btn ng-btn-primary ng-btn-block" name="registrar" type="submit">Crear cuenta &gt;</button>
        </form>
    <#elseif section = "info">
        ¿Ya tienes una cuenta?
        <a class="ng-link" href="${url.loginUrl}">Inicia sesión</a>
    </#if>
</@layout.registrationLayout>
