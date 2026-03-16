import { useMask } from "@react-input/mask";
 
export function useCpfMask() {
  return useMask({
    mask: "___.___.___-__",
    replacement: { _: /\d/ },
  });
}