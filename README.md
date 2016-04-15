# clj-base-crm

[![Clojars](https://img.shields.io/clojars/v/clj-base-crm.svg)](http://clojars.org/clj-base-crm)

clj-base-crm is a simple client library for accessing the Base CRM API.

Please read through the [Base CRM API Endpoint Reference](https://developers.getbase.com/docs/rest/articles/introduction).

## Installation

Add the following to your project.clj dependencies:

```clj
[clj-base-crm "0.2.0"]
```

## Usage

Require the `clj-base-crm.core` in your namespace:

```clj
(require '[clj-base-crm.core :as base])
```

Setup your Base Personal Access Tokens (PAT) [Don't have one? Read this](https://developers.getbase.com/docs/rest/articles/first_call).

```clj
(base/set-access-token! "YOUR_BASE_PAT_ACCESS_TOKEN")
```

clj-base-crm only supports the following resources:

`::base/leads`

`::base/contacts`

To create a new lead, use the `create` function.

```clj
(base/create ::base/leads
 {:first_name "John" :last_name "Doe" :email "john.doe@fakemail.com"})
```

To retrieve a new lead by it's ID, use the `retrieve` function.

```clj
(base/retrieve ::base/leads {:id 1234})
```

To update a lead by it's ID, use the `update` function.

```clj
(base/update ::base/leads {:id 1234} {:email "newemail@fakemail.com"})
```

To delete a lead by it's ID, use the `delete` function.

```clj
(base/delete ::base/leads  {:id 1234})
```

Sometimes you need to create a lead but you are not sure if this lead is already in the system. Base provides an upsert action which will create a new lead or update an existing lead based on the filters you pass in.
To upsert a lead by it's ID, use the `upsert` function.

This will upsert based on the email filter `john.doe@fakemail.com`.
```clj
(base/upsert ::base/leads
             {:email "john.doe@fakemail.com"}
             {:first_name "John" :last_name "Doe" :phone "916-456-7890"})
```

This will upsert based on the custom field filter `external_id` `123`.
```clj
(base/upsert ::base/leads
             {:custom_fields {:external_id 123}}
             {:first_name "John" :last_name "Doe" :phone "916-456-7890"})
```

## Return values

clj-base-crm returns the data received from the Base CRM API unaltered but the response will be converted from json to a Clojure map.

##Error handling

clj-base-crm does not throw exceptions on exceptional status codes from the Base API.  It returns the error body.

##Todo
clj-base-crm only exposes Leads and Contacts.  I plan to add more as I need them.  If you would like to expose more (just a simple hash-map), feel free to submit a PR!

- Support more Base resources
- Tests
- Documentation

## License

Copyright (c) 2016 Ryan Bertrand

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.