# Phase banner

A [GOV.UK element](https://govuk-elements.herokuapp.com/alpha-beta-banners/) for showing the current phase of the service and a feedback link.

## Usage

```
<div th:replace="fragments/phase-banner/phase-banner :: phase-banner (phase='ALPHA', link='mailto:test@test.com')"></div>
```
