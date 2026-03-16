import { cn } from "~/lib/utils";

interface GlobalSpinnerProps {
  className?: string;
  label?: string;
}
const cells = Array.from({ length: 4 });


export function GlobalSpinner({
  className,
  label = "Loading",
}: GlobalSpinnerProps) {
  return (
    <div
      className={cn(
        "fixed inset-0 z-50 flex items-center justify-center bg-background/80",
        className
      )}
    >
      <div className="rounded-none border-2 border-primary bg-card/95 px-6 py-5 shadow-[0_0_0_3px_color-mix(in_oklch,var(--background),transparent_20%),0_0_24px_color-mix(in_oklch,var(--primary),transparent_55%)]">
        <div className="mx-auto grid w-fit grid-cols-2 gap-1">
          {cells.map((_, index) => (
            <span
              key={index}
              className="size-3 bg-primary pixel-loader-cell"
              style={{ animationDelay: `${index * 140}ms` }}
            />
          ))}
        </div>
        <p className="mt-4 text-center text-[10px] text-primary retro-glow">
          {label}
        </p>
      </div>
    </div>
  );
}
