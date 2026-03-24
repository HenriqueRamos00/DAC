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
  label: React.ReactNode;
};

function FormField({
  children,
  className,
  description,
  error,
  label,
}: FormFieldProps) {
  return (
    <Field data-invalid={Boolean(error) || undefined} className={className}>
      <FieldLabel >{label}</FieldLabel>

      <FieldContent>
        {children}
        {description ? <FieldDescription>{description}</FieldDescription> : null}
        {error ? <FieldError>{error}</FieldError> : null}
      </FieldContent>
    </Field>
  );
}

export { FormField };
