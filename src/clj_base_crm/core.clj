(ns clj-base-crm.core
  (:refer-clojure :exclude [update])
  (:require [clj-http.client :as http]
            [clojure.string :as s]))

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

(defn ^:no-doc base-api-call
  "Sends a HTTP request to the Base CRM REST API.
  Returns the called URL and the response status in the metadata."
  [method partial-url & opts]
  (let [opts (into {} opts)
        url (str base-url partial-url)
        _ (println url)
        req (merge {:url                   url
                    :method                method
                    :oauth-token           *access-token*
                    :headers               {:accept "application/json"}
                    :coerce                :always
                    :as                    :json
                    :throw-entire-message? true
                    :content-type          :json} opts)
        {:keys [status body] :as response} (try
                                             (http/request req)
                                             (catch Exception e (ex-data e)))]
    (with-meta body {:status status
                     :url url})))

(defn- ^:no-doc custom-fields->query-params
  "Transforms map of custom fields into query-param friendly values
  {:external_id 123} => {\"custom_fields[external_id]\" 123}"
  [fields]
  (into {} (map #(let [[k v] %]
                  [(format "custom_fields[%s]" (name k)) v]) fields)))

(defn upsert-filters
  "Transform custom-field filters to be query-param friendly"
  [filters]
  (if-let [custom-fields (get filters :custom_fields)]
    (merge (dissoc filters :custom_fields)
           (custom-fields->query-params custom-fields))
    filters))

(defn- ^:no-doc replace-resource-ids
  "Given an endpoint and a hash-map, returns a new string with all
  keys in map found in input replaced with the value of the key"
  [endpoint m]
  (reduce
    (fn [acc [k v]] (s/replace acc (str ":" (name k)) (str v)))
    endpoint m))

(def api-spec
  "The spec of the Base CRM API which is currently supported by this library."
  ;TODO - Support more resources.  Updating this hash-map with more resources will just make it work.
  ;Feel free to submit a PR!  :)
  {::leads ["/v2/leads" "/v2/leads/:id"]
   ::contacts ["/v2/contacts" "/v2/contacts/:id"]})

(defn retrieve
  "Retrieve a single resource record.

   ##resource-ids

    `{:id 12345}` - Endpoints with no resource associations [(e.g. Leads)](https://developers.getbase.com/docs/rest/reference/leads)
    `{:deal_id 123 :contact_id 456}` - Endpoints with associations to other resources. [(e.g. Associated Contacts)](https://developers.getbase.com/docs/rest/reference/associated_contacts)

    [The resource-ids map to the Base API Endpoints](https://developers.getbase.com/docs/rest/articles/introduction)"
  [resource resource-ids]
  (when-let [[_ endpoint-with-ids] (get api-spec resource)]
    (base-api-call :get (replace-resource-ids endpoint-with-ids resource-ids))))

(defn retrieve-many
  "Retrieve a many resource records.  Use the params map to
   filter the records which record to retreive.

   ##resource-ids

    `{:id 12345}` - Endpoints with no resource associations [(e.g. Leads)](https://developers.getbase.com/docs/rest/reference/leads)
    `{:deal_id 123 :contact_id 456}` - Endpoints with associations to other resources. [(e.g. Associated Contacts)](https://developers.getbase.com/docs/rest/reference/associated_contacts)

    [The resource-ids map to the Base API Endpoints](https://developers.getbase.com/docs/rest/articles/introduction)"
  ([resource]
   (retrieve-many resource nil nil))
  ([resource params]
   (retrieve-many resource nil params))
  ([resource resource-ids {:keys [page per_page sort_by ids creator_id owner_id source_id] :as params}]
   (when-let [[endpoint endpoint-with-ids] (get api-spec resource)]
     (base-api-call :get (cond
                           (or (empty? resource-ids) (nil? resource-ids)) endpoint
                           (= 1 (count resource-ids)) (replace-resource-ids endpoint-with-ids resource-ids)
                           :else (replace-resource-ids endpoint-with-ids resource-ids))
                    {:query-params params}))))

(defn create
  "Create a new resource record.

   You should use the arity with resource-ids when the resource you are creating has an association to another resource. [(e.g. Associated Contacts)](https://developers.getbase.com/docs/rest/reference/associated_contacts)

   ##resource-ids

    `{:deal_id 123}` - Endpoints with associations to other resources. [(e.g. Associated Contacts)](https://developers.getbase.com/docs/rest/reference/associated_contacts)

    [The resource-ids map to the Base API Endpoints](https://developers.getbase.com/docs/rest/articles/introduction)"
  ([resource data]
   (create resource nil data))
  ([resource resource-ids data]
    ;Use this arity for resources with associations to other resources
   (when-let [[endpoint endpoint-with-ids] (get api-spec resource)]
     (base-api-call :post (if resource-ids
                            (replace-resource-ids endpoint-with-ids resource-ids)
                            endpoint)
                    {:form-params {:data data}}))))

(defn delete
  "Deletes an existing resource record.

   ##resource-ids

    `{:id 12345}` - Endpoints with no resource associations [(e.g. Leads)](https://developers.getbase.com/docs/rest/reference/leads)
    `{:deal_id 123 :contact_id 456}` - Endpoints with associations to other resources. [(e.g. Associated Contacts)](https://developers.getbase.com/docs/rest/reference/associated_contacts)

    [The resource-ids map to the Base API Endpoints](https://developers.getbase.com/docs/rest/articles/introduction)"
  [resource resource-ids]
  (when-let [[_ endpoint-with-ids] (get api-spec resource)]
    (base-api-call :delete (replace-resource-ids endpoint-with-ids resource-ids))))

(defn update
  "Updates an existing resource record.

   ##resource-ids

    `{:id 12345}` - Endpoints with no resource associations [(e.g. Leads)](https://developers.getbase.com/docs/rest/reference/leads)
    `{:deal_id 123 :contact_id 456}` - Endpoints with associations to other resources. [(e.g. Associated Contacts)](https://developers.getbase.com/docs/rest/reference/associated_contacts)

    [The resource-ids map to the Base API Endpoints](https://developers.getbase.com/docs/rest/articles/introduction)"
  [resource resource-ids data]
  (when-let [[_ endpoint-with-ids] (get api-spec resource)]
    (base-api-call :put (replace-resource-ids endpoint-with-ids resource-ids)
                   {:form-params {:data data}})))

(defn upsert
  "Create a new resource record or update an existing resource record, based on a value of a filter or a set of filters.
  At least one filter is required.

  ##Behaviour

  - If multiple records match a set of filters, the request will return an error - 409.
  - If a single record matches, then the existing record is updated
  - If no record matches the query, a new record is created.

  ##filters

  `{:email \"john.doe@fakemail.com\"}` - Filters by email
  `{:custom_fields {:external_id 123}}` - Filters by the custom field 'external_id'"
  [resource {:keys [data filters]}]
  (when-let [[endpoint _] (get api-spec resource)]
    (base-api-call :post (str endpoint "/upsert")
                   {:query-params (upsert-filters filters)
                    :form-params  {:data data}})))