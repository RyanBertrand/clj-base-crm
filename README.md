# clj-base-crm

[![Clojars](https://img.shields.io/clojars/v/clj-base-crm.svg)](http://clojars.org/clj-base-crm)

clj-base-crm is a client library for accessing the Base CRM API. clj-base-crm separates each resource in it's own namespace for ease of use.

Please read through the [Base CRM API Endpoint Reference](https://developers.getbase.com/docs/rest/articles/introduction).

## Installation

Add the following to your project.clj dependencies:

```clj
[clj-base-crm "0.1.0"]
```

## Usage

Require the Base resource you would like to interact with in the normal way:

```clj
(require '[clj-base-crm.leads :as base-leads])
```

Setup your Base Personal Access Tokens (PAT) ([Don't have one? Read this](https://developers.getbase.com/docs/rest/articles/first_call).

```clj
(clj-base-crm/set-access-token! "YOUR_BASE_PAT_ACCESS_TOKEN")
```

To create a new lead, use the `create` function.

```clj
(base-leads/create
 {:first_name "John" :last_name "Doe" :email "john.doe@fakemail.com"})
```

To retrieve a new lead by it's ID, use the `retrieve` function.

```clj
(base-leads/retrieve 1234)
```

To update a lead by it's ID, use the `update` function.

```clj
(base-leads/update 1234 {:email "newemail@fakemail.com"})
```

To delete a lead by it's ID, use the `delete` function.

```clj
(base-leads/delete 1234)
```

Sometimes you need to create a lead but you are not sure if this lead is already in the system. Base provides an upsert action which will create a new lead or update an existing lead based on the filters you pass in.
To upsert a lead by it's ID, use the `upsert` function.

This will upsert based on the email filter `john.doe@fakemail.com`.
```clj
(base-leads/upsert {:email "john.doe@fakemail.com"} {:first_name "John" :last_name "Doe" :phone "916-456-7890"})
```

## Return values

clj-base-crm returns the data received from the Base CRM API unaltered but the response will be converted from json to a Clojure map.

##Error handling

clj-base-crm throws exceptions on exceptional status codes from the Base API.

##Todo
clj-base-crm only exposes Leads and Contacts.  I plan to add more as I need them.  If you would like to expose more, feel free to submit a PR!

- Support more Base resources
- Tests
- Documentation

## License

Copyright (c) 2016 Ryan Bertrand

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.