(ns clj-base-crm.core
  (:refer-clojure :exclude [update])
  (:require [clj-http.client :as http]
            [clojure.tools.logging :as log]))

;API Docs: https://developers.getbase.com/docs/rest/articles/first_call

(def ^:no-doc base-url "https://api.getbase.com")

;Base CRM API Token
(def ^:no-doc ^:dynamic *access-token* "YOUR_BASE_PAT_ACCESS_TOKEN")

(defn set-access-token!
  "Set the Access Token to be used by default on all requests."
  [token]
  (def ^:dynamic *access-token* token))

(defmacro with-token
  "Allows you to make API calls using this access token instead of the global token.
  Useful for many users scenarios."
  [token & body]
  `(binding [*access-token* ~token]
     (do ~@body)))

(defn ^:no-doc send-request!
  "Sends a http request with formatted params"
  [method partial-url & opts]
  (let [opts (into {} opts)
        url (str base-url partial-url)
        req (merge {:url                   url
                    :method                method
                    :oauth-token           *access-token*
                    :headers               {:accept "application/json"}
                    :coerce                :always
                    :as                    :json
                    :throw-entire-message? true
                    :content-type          :json} opts)
        {:keys [body] :as response} (http/request req)
        _ (println response)]
    (if body
      body
      (log/error "Error from Base CRM API:" (:error response)))))

;Helpers

(defn custom-fields->query-params
  "Transforms map of custom fields into query-param friendly values
  {:external_id 123} => {\"custom_fields[external_id]\" 123}"
  [fields]
  (into {} (map #(let [[k v] %]
                  [(format "custom_fields[%s]" (name k)) v]) fields)))

(defn upsert-filters
  "Transform custom-field filters to be query-param friendly"
  [filters]
  (if-let [custom-fields (get filters :custom_fields)]
    ;Transform the custom-fields into query-params
    (merge (dissoc filters :custom_fields)
           (custom-fields->query-params custom-fields))
    filters))

;Common
;You can use these to use resources which I have not yet implemented.

(defn retrieve-all
  "Base to retrieve all (or filtered) records, according to the parameters"
  ([partial-url]
   (retrieve-all partial-url nil))
  ([partial-url {:keys [page per_page sort_by ids creator_id owner_id source_id] :as parameters}]
   (send-request! :get partial-url
                  {:query-params parameters})))

(defn retrieve
  "Base to retrieve a single record"
  [partial-url]
  (send-request! :get partial-url))

(defn create
  "Base to create a new record"
  [partial-url data]
  (send-request! :post partial-url
                 {:form-params {:data data}}))

(defn update
  "Base to update a record"
  [partial-url data]
  (send-request! :put partial-url
                 {:form-params {:data data}}))

(defn delete
  "Base to delete an existing record"
  [partial-url]
  (send-request! :delete partial-url))

(defn upsert
  "Base to create a new record or update an existing record, based on a value of a filter
   or a set of filters. At least one filter - query parameter - is required."
  [partial-url filters data]
  (send-request! :post partial-url
                 {:query-params (upsert-filters filters)
                  :form-params  {:data data}}))