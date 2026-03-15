import { Fragment } from "react";
import { Link } from "react-router";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "./ui/breadcrumb";

export type AppBreadcrumbItem = {
  label: string;
  href?: string;
};

interface AppBreadcrumbProps {
  items: AppBreadcrumbItem[];
}

export function AppBreadcrumb({ items }: AppBreadcrumbProps) {
  if (!items.length) return null;

  return (
    <Breadcrumb>
      <BreadcrumbList>
        {items.map((item, index) => {
          const isLast = index === items.length - 1;

          return (
            <BreadcrumbItem className="flex items-center gap-2 text-xs text-muted-foreground mb-4" key={`${item.label}-${index}`}>
              {!isLast && item.href ? (
                <>
                  <BreadcrumbLink href={item.href}>{item.label}</BreadcrumbLink>
                  <BreadcrumbSeparator />
                </>
              ) : (
                <BreadcrumbPage className="text-primary">{item.label}</BreadcrumbPage>
              )}
            </BreadcrumbItem>
          );
        })}
      </BreadcrumbList>
    </Breadcrumb>
  );
}
