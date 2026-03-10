import { Select as BaseUiSelect } from '@base-ui/react/select';
import { Field } from '@base-ui/react/field';
import { ChevronsUpDown, Check } from 'lucide-react';

export interface SelectOption {
  value: string;
  label: string;
}

export interface SelectProps {
  id?: string;
  label?: string;
  placeholder?: string;
  options: SelectOption[];
  multiple?: boolean;
  defaultValue?: string | string[];
  value?: string | string[];
  onValueChange?: (value: any) => void;
  disabled?: boolean;
  className?: string;
}

export function Select({
  id,
  label,
  placeholder = 'Selecione...',
  options,
  multiple = false,
  defaultValue,
  value,
  onValueChange,
  disabled,
  className = '',
}: SelectProps) {
  const renderValue = (selectedValue: string | string[]) => {
    if (!selectedValue || selectedValue.length === 0) {
      return placeholder;
    }

    if (Array.isArray(selectedValue)) {
      const firstOption = options.find((opt) => opt.value === selectedValue[0]);
      const additional = selectedValue.length > 1 ? ` (+${selectedValue.length - 1})` : '';
      return (firstOption?.label || selectedValue[0]) + additional;
    }

    const option = options.find((opt) => opt.value === selectedValue);
    return option?.label || selectedValue;
  };

  return (
    <Field.Root className="flex w-full flex-col gap-1.5">
      {label && <Field.Label className="text-sm font-medium text-slate-300">{label}</Field.Label>}

      <BaseUiSelect.Root
        multiple={multiple}
        defaultValue={defaultValue as any}
        value={value as any}
        onValueChange={onValueChange}
        disabled={disabled}
      >
        <BaseUiSelect.Trigger
          id={id}
          className={`flex h-10 w-full items-center justify-between bg-[#242b3d] border border-slate-700 px-3 py-2 text-sm text-slate-300 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 rounded-none ${className}`}
        >
          <BaseUiSelect.Value className="truncate">{renderValue}</BaseUiSelect.Value>
          <BaseUiSelect.Icon className="ml-2 text-slate-400">
            {/* 2. Usando o ChevronsUpDown do Lucide */}
            <ChevronsUpDown className="h-4 w-4 opacity-50" />
          </BaseUiSelect.Icon>
        </BaseUiSelect.Trigger>

        <BaseUiSelect.Portal>
          <BaseUiSelect.Positioner sideOffset={4} alignItemWithTrigger={false} className="z-50">
            <BaseUiSelect.Popup className="max-h-60 w-[var(--anchor-width)] overflow-auto rounded-none border border-slate-700 bg-[#242b3d] p-1 text-slate-300 shadow-lg focus:outline-none">
              {options.map((opt) => (
                <BaseUiSelect.Item
                  key={opt.value}
                  value={opt.value}
                  className="relative flex w-full cursor-pointer BaseUiSelect-none items-center rounded-sm py-1.5 pl-8 pr-2 text-sm outline-none focus:bg-slate-700 focus:text-white data-[highlighted]:bg-slate-700 data-[disabled]:pointer-events-none data-[disabled]:opacity-50"
                >
                  <BaseUiSelect.ItemIndicator className="absolute left-2 flex h-3.5 w-3.5 items-center justify-center">
                    {/* 3. Usando o Check do Lucide */}
                    <Check className="h-4 w-4" />
                  </BaseUiSelect.ItemIndicator>
                  <BaseUiSelect.ItemText>{opt.label}</BaseUiSelect.ItemText>
                </BaseUiSelect.Item>
              ))}
            </BaseUiSelect.Popup>
          </BaseUiSelect.Positioner>
        </BaseUiSelect.Portal>
      </BaseUiSelect.Root>
    </Field.Root>
  );
}
