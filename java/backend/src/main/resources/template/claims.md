<p align="center"><img src="https://storage.googleapis.com/hmcts-images/Screenshot%202021-03-02%20at%2020.39.09.png" alt="drawing" width="800"/></p>

| Claimants      | Defendants |
| ----------- | ----------- |
{% for t in range(0, max(claim.parties.claimants | length, claim.parties.defendants | length) - 1) %}
| {{claim.parties.claimants[t].name}} | {{claim.parties.defendants[t].name}}|
{% endfor %}

### Claim value £{{claim.lowerAmount | numberformat("#,###")}} - £{{claim.higherAmount | numberformat("#,###")}}


{% if claim.availableEvents | length > 0 %}
## Available actions

<div class="hmcts-menu">
    <div class="hmcts-menu__wrapper">
{% for event in claim.availableEvents %}
    <a href="/cases/case-details/{{caseId}}/trigger/claims_{{event}}_{{claim.claimId}}">
    <p class="govuk-button hmcts-menu__item" data-module="govuk-button">
        Confirm service
    </p>
    </a>
{% endfor %}
    </div>
</div>
{% endif %}

