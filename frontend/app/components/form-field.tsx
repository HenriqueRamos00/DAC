import * as React from "react";

import {
  Field,
  FieldContent,
  FieldDescription,
  FieldError,
  FieldLabel,
} from "~/components/ui/field";

type FormFieldProps = {
  children: React.ReactNode;
  className?: string;
  description?: React.ReactNode;
  error?: string;
  htmlFor?: string;
  label: React.ReactNode;
  required?: boolean;
};

function FormField({
  children,
  className,
  description,
  error,
  htmlFor,
  label,
  required = false,
}: FormFieldProps) {
  return (
    <Field data-invalid={Boolean(error) || undefined} className={className}>
      <FieldLabel htmlFor={htmlFor}>
        {label}
        {required ? " *" : ""}
      </FieldLabel>

      <FieldContent>
        {children}
        {description ? <FieldDescription>{description}</FieldDescription> : null}
        {error ? <FieldError>{error}</FieldError> : null}
      </FieldContent>
    </Field>
  );
}

export { FormField };
