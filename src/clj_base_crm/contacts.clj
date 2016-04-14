(ns clj-base-crm.contacts
  (:refer-clojure :exclude [update])
  (:require [clj-base-crm.core :as base]))

;API Docs: https://developers.getbase.com/docs/rest/reference/contacts

(def endpoint "/v2/contacts")

(defn retrieve-all
  "Retrieve all contacts according to the parameters"
  ([]
   (base/retrieve-all nil))
  ([{:keys [page per_page sort_by ids creator_id owner_id source_id] :as parameters}]
   (base/retrieve-all endpoint parameters)))

(defn retrieve
  "Retrieve a single contact"
  [id]
  (base/retrieve (format "%s/%s" endpoint id)))

(defn create
  "Creates a new contact"
  [data]
  (base/create endpoint data))

(defn update
  "Updates contact information"
  [id data]
  (base/update (format "%s/%s" endpoint id) data))

(defn delete
  "Delete an existing contact"
  [id]
  (base/delete (format "%s/%s" endpoint id)))

(defn upsert
  "Create a new contact or update an existing, based on a value of a filter
   or a set of filters. At least one filter is required."
  [filters data]
  (base/upsert (format "%s/upsert" endpoint) filters data))