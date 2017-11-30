# Client side validation

The validations that are executed in the front-end are hooking into the native HTML5 constraint validations when available.
You can [read more about these constraint validations on the mozilla developer network](https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/HTML5/Constraint_validation)

## Defining your own messages
You can define your own error message by adding an `data-{constraint}-errormessage` attribute to the template.
An example of this:

```
<input type="text" required="required" data-required-errormessage="this field cannot be empty"/>
```

Most of the times these attributes will come from `ValidationMessages.properties`, we can get these properties with thymeleaf with the following th:attr logic:

```
<input type="text" required="required" th:attr="data-required-errormessage=#{validation.field.must.not.be.blank}" />
```

If there is no attribute supplied it will grab a default message defined in formValidation.js.


## Be careful with mixing certain types and attributes
One thing to notice is that these constraints cannot always be mixed with any type of input.
I.e. an `<input type="number" />` cannot have a maxlength attribute, but can have a max attribute, for an `<input type="text" />` it is the reverse.
For reference what kind of field can use what there is [the html5 spec](https://www.w3.org/TR/html5/forms.html#input-type-attr-summary).

## Hiding errormessages
there is a small api for hiding error messages, there are three options which can be set with data attribute data-{constraint}-showmessage="".
The values it can have are:

* visuallyhidden, this will hide it, but will make it available for screen readers
* none, doesn't output an error message at all
* show, the default

## Getting the status of an input

You can also get the status of an input within the scope of a <div class="form-group">, this can be done by setting data-{constraint}-status on an element. It will then give a data-status="true" or data-status="false" back on the same element. For an example of this you can see `Advanced use cases` in the html.  
