<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html lang="${(locale.currentLanguageTag)!'es'}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="robots" content="noindex, nofollow">
    <title>NutriGO — Nutrición inteligente. Progreso real.</title>
    <link rel="icon" href="${url.resourcesPath}/img/favicon.ico">
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet">
        </#list>
    </#if>
    <#if properties.scripts?has_content>
        <#list properties.scripts?split(' ') as script>
            <script src="${url.resourcesPath}/${script}" defer></script>
        </#list>
    </#if>
</head>

<body class="ng-body ${bodyClass}">
<div class="ng-shell">
    <div class="ng-card">

        <!-- Columna izquierda: formulario -->
        <section class="ng-form-col">
            <div>
                <div class="ng-logo">
                    <div class="ng-logo-mark">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9 9 0 01-9-9c0-4.97 4.03-9 9-9 4.97 0 9 4.03 9 9 0 1.25-.26 2.44-.73 3.52M12 3v18m0-9h9"/>
                            <path stroke-linecap="round" stroke-linejoin="round" d="M9 14l2 2 4-4"/>
                        </svg>
                    </div>
                    <span class="ng-logo-text">Nutri<span>GO</span></span>
                </div>

                <div class="ng-heading">
                    <p class="ng-eyebrow">Tu bienestar empieza aquí</p>
                    <h1 class="ng-title"><#nested "header"></h1>
                </div>

                <#if auth?has_content && auth.showUsername() && !auth.showResetCredentials()>
                    <div class="ng-attempted">
                        <span>${auth.attemptedUsername}</span>
                        <a href="${url.loginRestartFlowUrl}">Cambiar de cuenta</a>
                    </div>
                </#if>

                <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
                    <div class="ng-alert ng-alert-${message.type}" role="alert">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="8" x2="12" y2="12"/>
                            <line x1="12" y1="16" x2="12.01" y2="16"/>
                        </svg>
                        <span>${kcSanitize(message.summary)?no_esc}</span>
                    </div>
                </#if>

                <div class="ng-content">
                    <#nested "form">

                    <#if auth?has_content && auth.showTryAnotherWayLink()>
                        <form id="kc-select-try-another-way-form" action="${url.loginAction}" method="post">
                            <input type="hidden" name="tryAnotherWay" value="on">
                            <a href="#" class="ng-link" onclick="document.forms['kc-select-try-another-way-form'].submit();return false;">${msg("doTryAnotherWay")}</a>
                        </form>
                    </#if>

                    <#nested "socialProviders">
                </div>
            </div>

            <#if displayInfo>
                <div class="ng-info">
                    <#nested "info">
                </div>
            </#if>
        </section>

        <!-- Columna derecha: panel promocional (igual que el login de Angular) -->
        <aside class="ng-promo" aria-hidden="true">
            <div class="ng-glow ng-glow-a"></div>
            <div class="ng-glow ng-glow-b"></div>

            <div class="ng-promo-body">
                <div class="ng-badge">
                    <svg viewBox="0 0 20 20" fill="currentColor"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/></svg>
                    <span>POTENCIADO POR IA</span>
                </div>

                <h2 class="ng-promo-title">Nutrición inteligente.<br><span>Progreso real.</span></h2>
                <p class="ng-promo-text">Seguimiento de IMC y nutrición personalizada que evoluciona contigo.</p>

                <div class="ng-preview">
                    <div class="ng-preview-top">
                        <div class="ng-preview-leaf">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 21a9 9 0 01-9-9c0-4.97 4.03-9 9-9 4.97 0 9 4.03 9 9 0 1.25-.26 2.44-.73 3.52M12 3v18"/></svg>
                        </div>
                        <div class="ng-preview-dot"></div>
                    </div>
                    <div class="ng-preview-grid">
                        <div>
                            <p class="ng-preview-label">IMC Actual</p>
                            <p class="ng-preview-value">22.5</p>
                            <span class="ng-preview-pill">Normal</span>
                        </div>
                        <div class="ng-right">
                            <p class="ng-preview-label">Meta de peso</p>
                            <p class="ng-preview-goal">65 kg</p>
                        </div>
                    </div>
                    <div class="ng-preview-progress">
                        <span>PROGRESO</span>
                        <strong>-4.2 kg</strong>
                    </div>
                </div>
            </div>

            <ul class="ng-features">
                <li>
                    <span class="ng-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 7h6m0 10v-3m-3 3v-6m-3 6v-1m6-9a2 2 0 012 2v10a2 2 0 01-2 2H7a2 2 0 01-2-2V7a2 2 0 012-2h2"/></svg></span>
                    <span>Calcula tu IMC y conoce tu categoría al instante</span>
                </li>
                <li>
                    <span class="ng-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/></svg></span>
                    <span>Registra tu peso y sigue tu evolución semana a semana</span>
                </li>
                <li>
                    <span class="ng-feature-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg></span>
                    <span>Recibe el plan y los reportes de tu nutricionista</span>
                </li>
            </ul>
        </aside>

    </div>
</div>
</body>
</html>
</#macro>
