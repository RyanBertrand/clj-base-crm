(ns clj-base-crm.leads
  (:refer-clojure :exclude [update])
  (:require [clj-base-crm.core :as base]))

;API Docs: https://developers.getbase.com/docs/rest/reference/leads

(def endpoint "/v2/leads")

(defn retrieve-all
  "Retrieve all leads according to the parameters"
  ([]
   (base/retrieve-all nil))
  ([{:keys [page per_page sort_by ids creator_id owner_id source_id] :as parameters}]
   (base/retrieve-all endpoint parameters)))

(defn retrieve
  "Retrieve a single lead"
  [id]
  (base/retrieve (format "%s/%s" endpoint id)))

(defn create
  "Creates a new lead"
  [data]
  (base/create endpoint data))

(defn update
  "Updates lead information"
  [id data]
  (base/update (format "%s/%s" endpoint id) data))

(defn delete
  "Delete an existing lead"
  [id]
  (base/delete (format "%s/%s" endpoint id)))

(defn upsert
  "Create a new lead or update an existing, based on a value of a filter
   or a set of filters. At least one filter is required."
  [filters data]
  (base/upsert (format "%s/upsert" endpoint) filters data))