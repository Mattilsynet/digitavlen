# Digitavlen

Et dashbord for Team Servering med innsikt i f.eks. git historikken til repoene.

## Utvikling

- Spinn opp et REPL i Emacs.
- Evaluer `(dev/start)` i `dev/digitavlen/dev.clj`.
- Den vil nok spørre om tilgang til git ssh-en nå. Dette trenger den for å klone
  ned prosjektene til maskinen din.
- Dette kan ta sin tid. Den driver nå med å klone, parse og aggregere på
  dataene.
- Du kan følge med på processen med `C-c C-z`.
- Når denne, `"Powerpack started on port 5050"`, meldingen kommer kan du åpne
  localhost:5050 og nyte de fine dataene 📊💅.

## Produksjonsmiljø og sjøsetting

Digitavlen kjører i et [Docker image](./docker) med nginx på Google Cloud Run.
Miljøet er konfigurert av Terraform.

### Bygge prosjektet for produksjon

```sh
make docker/build
```

Deretter kan du sjekke at ting virkelig er produksjonsklart:

```sh
http-server docker/build
```

### Oppsett av produksjonsmiljøet

Du må ha noen verktøy:

```sh
brew install terraform
```

For å sette opp miljøet må du ha en GCP-konto og tilgang til relevante
prosjekter.

Du må være autentisert mot GCP:

```sh
gcloud auth login
gcloud auth application-default login
```

Da er du klar for å kjøre opp ting:

```sh
cd tf
terraform init
terraform plan
terraform apply
```

Dette vil sette opp nødvendig infrastruktur. Merk at [terraform-oppsettet
vårt](./tf/main.tf) har et "hello world" image. Dette imaget brukes kun ved
første gangs oppsett. [Github
Actions-arbeidsflyten](.github/workflows/build.yml) ber CloudRun om å kjøre nye
images ved push.

### Github Actions

Verdt å merke seg: prosjekt-id-en som brukes med `workload_identity_provider`
når vi autentiserer oss mot GCP for å oppdatere CloudRun-konfigurasjonen vår kan
finnes på følgene vis:

```sh
gcloud projects list \
  --filter="$(gcloud config get-value project)" \
  --format="value(PROJECT_NUMBER)"
```

## Tester

Testene kan kjøres med:

```sh
make test
```
