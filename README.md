# cljq-upload

This is an example app showing how to use the [jQuery-File-Upload](https://github.com/blueimp/jQuery-File-Upload) widget to upload files directly to s3 using cors. I basically translated [this](http://pjambet.github.com/blog/direct-upload-to-s3) tutorial into clojure.

## Instructions

Clone this repo, set the following config variables at the top of `cljq-upload.handler`:

    (def s3-cred
      {:secret-key "YOUR SECRET KEY"
      :access-key "YOUR ACCESS KEY"})

    (def bucket-name "YOUR BUCKET NAME")`
     
Then run:
      
      lein ring server

## License

Copyright © 2013 FIXME
