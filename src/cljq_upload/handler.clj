(ns cljq-upload.handler
  (:use compojure.core
        hiccup.page
        hiccup.form
        [ring.util.codec :only [base64-encode]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.multipart-params]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.data.json :as json])
  (:import (javax.crypto Mac
                         spec.SecretKeySpec)))

(def s3-cred
  {:secret-key "YOUR SECRET KEY"
   :access-key "YOUR ACCESS KEY"})

(def bucket-name "YOUR BUCKET NAME")

(defn page []
  (html5
   [:head
    (include-css "http://blueimp.github.com/cdn/css/bootstrap.min.css"
                 "/css/style.css"
                 "/css/jquery.fileupload-ui.css")]
   [:body
    (form-to {:class "direct-upload"
              :enctype "multipart/form-data"}
             [:post (str "https://" bucket-name ".s3.amazonaws.com") ]
             (hidden-field "key")
             (hidden-field "AWSAccessKeyId" (:access-key s3-cred))
             (hidden-field "acl" "public-read")
             (hidden-field "policy")
             (hidden-field "signature")
             (hidden-field "success_action_status" "201")
             
             [:input {:type "file" :name "file" :multiple "multiple"}]
             [:input {:type "submit"}]
             [:div.progress.progress-striped.active
              [:div.bar]])

    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"
                    "/js/vendor/jquery.ui.widget.js"
                    "/js/jquery.fileupload.js"
                    "/js/main.js"
                    )]))

(def s3-policy-document (s/replace (json/write-str 
                            {:expiration "2013-01-25T00:00:00Z" ;;todo: generate this
                             :conditions [
                                          {:bucket bucket-name}
                                          {:acl "public-read"}
                                          ["starts-with" "$key" "uploads/"]
                                          {:success_action_status "201"}
                                          ["content-length-range" 0 11048576]]
                             })
                           #"\n|\r"
                           ""))

(defn generate-policy [policy-json]
  (s/replace 
   (base64-encode (.getBytes  policy-json "UTF-8"))
   #"\n|\r" ""))

(defn generate-signature [secret-key policy]
  (let [hmac (Mac/getInstance "HmacSHA1")]
    (.init hmac (SecretKeySpec. (.getBytes (:secret-key s3-cred) "UTF-8")
                                "HmacSHA1"))
    (s/replace 
     (base64-encode (.doFinal hmac (.getBytes policy "UTF-8")))
     #"\n|\r" "")))

(defn signed-urls [title]
  (let [policy (generate-policy s3-policy-document)] 
    (json/write-str
     {:policy policy
      :signature (generate-signature (:secret-key s3-cred) policy)
      :key (str "uploads/" title)
      :success-action-redirect "/"})))

(defroutes app-routes
  (route/resources "/")
  (GET "/" [] (page))
  (GET "/signed_urls" [title] (signed-urls title))
  (route/not-found "Not Found")
  )

(def app
  (handler/site app-routes))
