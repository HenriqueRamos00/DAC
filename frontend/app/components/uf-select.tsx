import { cn } from "~/lib/utils";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "~/components/ui/select";

export const UF_OPTIONS = [
  "AC","AL","AP","AM","BA","CE","DF","ES","GO","MA","MT","MS","MG",
  "PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO",
] as const;

interface UfSelectProps {
  id?: string;
  label?: string;
  name?: string;
  required?: boolean;
  value?: string;
  onValueChange?: (value: string) => void;
  className?: string;
}

export function UfSelect({
  id,
  label = "UF",
  name,
  required = false,
  value,
  onValueChange,
  className,
}: UfSelectProps) {
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={id} className="text-sm font-medium">
        {label}
        {required ? " *" : ""}
      </label>
      {name ? <input type="hidden" name={name} value={value ?? ""} /> : null}
      <Select value={value} onValueChange={(v) => { if (v) onValueChange?.(v); }}>
        <SelectTrigger id={id} className={cn("w-full", className)}>
          <SelectValue placeholder="UF..." />
        </SelectTrigger>
        <SelectContent alignItemWithTrigger={false} className="max-h-48">
          {UF_OPTIONS.map((uf) => (
            <SelectItem key={uf} value={uf}>
              {uf}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}
