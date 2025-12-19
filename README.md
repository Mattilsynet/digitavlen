# Digitavlen

Et dashbord for Team Servering med innsikt i f.eks. git historikken til repoene.

## Utvikling

- Spinn opp et REPL i Emacs.
- Evaluer `(dev/start)` i `digitavlen.dev`-navnerommet
- Den vil nok spørre om tilgang til git ssh-en nå. Dette trenger den for å klone
  ned prosjektene til maskinen din.
- Dette kan ta sin tid. Den driver nå med å klone, parse og aggregere på
  dataene.
- Du kan følge med på processen med `C-c C-z`.
- Når denne, `"Powerpack started on port 5050"`, meldingen kommer kan du åpne
  localhost:5050 og nyte de fine dataene 📊💅.

## Bygg release

Kjør `clj -X:build`
