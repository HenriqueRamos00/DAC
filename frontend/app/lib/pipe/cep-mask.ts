import { useMask } from "@react-input/mask";
 
export function useCepMask() {
  return useMask({
    mask: "__.___-___",
    replacement: { _: /\d/ },
  });
}