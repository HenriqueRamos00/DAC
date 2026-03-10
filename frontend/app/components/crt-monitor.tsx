import { useRef, useEffect } from "react";
import { Link } from "react-router";

export interface CrtButton {
  label: string;
  fillColor: string;
  to: string;
}

export interface CrtMonitorProps {
  title: string;
  subtitle?: string;
  buttons?: CrtButton[];
  footer?: string;
  footerColor?: string;
  children?: React.ReactNode;
}

const VERTEX_SHADER = `
  attribute vec4 aVertexPosition;
  attribute vec2 aTextureCoord;
  varying lowp vec2 vTextureCoord;
  void main(void) {
    gl_Position   = aVertexPosition;
    vTextureCoord = aTextureCoord;
  }
`;

const FRAGMENT_SHADER = `
  precision lowp float;
  varying vec2 vTextureCoord;
  uniform sampler2D uSampler;
  uniform float uTime;
  uniform vec2 uResolution;

  float easeOutQuad(float t){ return t*(2.0-t); }

  void main() {
    vec2 uv = vTextureCoord;

    float dur  = 3000.0;
    float bend = easeOutQuad(min(uTime/dur,1.0));
    vec2 cuv = uv*2.0-1.0;
    cuv *= 1.0 + 0.1*bend;
    cuv *= 1.0 - 0.085*bend + 0.05*bend*pow(abs(cuv.yx),vec2(2.0));
    cuv = cuv*0.5+0.5;

    if (any(lessThan(cuv, vec2(0.))) || any(greaterThan(cuv, vec2(1.)))){
      gl_FragColor = vec4(0.0);
      return;
    }

    float decay = .005*exp(-uTime/750.);
    float r = texture2D(uSampler, vec2(cuv.x+decay, cuv.y)).r;
    float g = texture2D(uSampler, cuv).g;
    float b = texture2D(uSampler, vec2(cuv.x-decay, cuv.y)).b;
    vec4 texColor = vec4(r,g,b,1.0);

    float scan = max(0., sin((cuv.y+uTime*0.0000005)*uResolution.y)) * 0.5;
    texColor.rgb = mix(texColor.rgb, texColor.rgb - vec3(scan), 0.4);

    float vig = 1.0 - length(cuv-0.5) * .7;
    texColor.rgb *= vig;
    texColor.rgb *= 2.5;
    texColor.rgb  = 1.0 - exp(-texColor.rgb);

    gl_FragColor = texColor;
  }
`;

const CANVAS_W = 1280;
const CANVAS_H = 720;
const BTN_W = 220;
const BTN_H = 60;
const BTN_GAP = 30;

function compileShader(gl: WebGL2RenderingContext, type: number, src: string) {
  const shader = gl.createShader(type)!;
  gl.shaderSource(shader, src);
  gl.compileShader(shader);
  if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
    const info = gl.getShaderInfoLog(shader);
    gl.deleteShader(shader);
    throw new Error(`Shader compile error: ${info}`);
  }
  return shader;
}

function getButtonLayout(count: number) {
  const totalW = count * BTN_W + (count - 1) * BTN_GAP;
  const startX = (CANVAS_W - totalW) / 2;
  const y = CANVAS_H / 2 + 60;
  return Array.from({ length: count }, (_, i) => ({
    x: startX + i * (BTN_W + BTN_GAP),
    y,
    w: BTN_W,
    h: BTN_H,
  }));
}

function drawSource(
  ctx: CanvasRenderingContext2D,
  props: CrtMonitorProps,
) {
  const { title, subtitle, buttons = [], footer, footerColor = "#FFB800" } = props;
  const w = CANVAS_W;
  const h = CANVAS_H;

  ctx.fillStyle = "#0A0A0A";
  ctx.fillRect(0, 0, w, h);
  ctx.textAlign = "center";

  ctx.fillStyle = "#00ff41";
  ctx.shadowColor = "#00ff41";
  ctx.shadowBlur = 15;
  ctx.font = "bold 96px 'VT323', monospace";
  ctx.fillText(title, w / 2, h / 2 - 60);

  if (subtitle) {
    ctx.font = "52px 'VT323', monospace";
    ctx.globalAlpha = 0.8;
    ctx.fillText(subtitle, w / 2, h / 2 + 10);
    ctx.globalAlpha = 1.0;
  }
  ctx.shadowBlur = 0;

  const layout = getButtonLayout(buttons.length);
  buttons.forEach((btn, i) => {
    const pos = layout[i];
    ctx.fillStyle = btn.fillColor;
    ctx.strokeStyle = "#fff";
    ctx.lineWidth = 3;
    ctx.fillRect(pos.x, pos.y, pos.w, pos.h);
    ctx.strokeRect(pos.x, pos.y, pos.w, pos.h);

    ctx.fillStyle = "#fff";
    ctx.font = "bold 24px 'Press Start 2P', cursive";
    ctx.fillText(btn.label, pos.x + pos.w / 2, pos.y + pos.h / 2 + 12);
  });

  if (footer) {
    ctx.font = "32px 'VT323', monospace";
    ctx.fillStyle = footerColor;
    ctx.shadowColor = footerColor;
    ctx.shadowBlur = 10;
    ctx.fillText(footer, w / 2, h - 80);
    ctx.shadowBlur = 0;
  }
}

export function CrtMonitor(props: CrtMonitorProps) {
  const { buttons = [], children } = props;
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const wrapperRef = useRef<HTMLDivElement>(null);
  const frameRef = useRef<number>(0);

  useEffect(() => {
    const canvas = canvasRef.current;
    const wrapper = wrapperRef.current;
    if (!canvas || !wrapper) return;

    const gl = canvas.getContext("webgl2", { alpha: true });
    if (!gl) return;

    const src = document.createElement("canvas");
    src.width = CANVAS_W;
    src.height = CANVAS_H;
    const srcCtx = src.getContext("2d")!;
    Promise.all([
      document.fonts.load("96px 'VT323'"),
      document.fonts.load("bold 24px 'Press Start 2P'"),
    ]).then(() => {
      drawSource(srcCtx, props);
    });

    const vs = compileShader(gl, gl.VERTEX_SHADER, VERTEX_SHADER);
    const fs = compileShader(gl, gl.FRAGMENT_SHADER, FRAGMENT_SHADER);
    const prog = gl.createProgram()!;
    gl.attachShader(prog, vs);
    gl.attachShader(prog, fs);
    gl.linkProgram(prog);
    gl.useProgram(prog);

    const quadPos = new Float32Array([-1, 1, 1, 1, -1, -1, 1, -1]);
    const quadUV = new Float32Array([0, 0, 1, 0, 0, 1, 1, 1]);

    const bufPos = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, bufPos);
    gl.bufferData(gl.ARRAY_BUFFER, quadPos, gl.STATIC_DRAW);
    const aPos = gl.getAttribLocation(prog, "aVertexPosition");
    gl.enableVertexAttribArray(aPos);
    gl.vertexAttribPointer(aPos, 2, gl.FLOAT, false, 0, 0);

    const bufUV = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, bufUV);
    gl.bufferData(gl.ARRAY_BUFFER, quadUV, gl.STATIC_DRAW);
    const aUV = gl.getAttribLocation(prog, "aTextureCoord");
    gl.enableVertexAttribArray(aUV);
    gl.vertexAttribPointer(aUV, 2, gl.FLOAT, false, 0, 0);

    const tex = gl.createTexture();
    gl.bindTexture(gl.TEXTURE_2D, tex);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);

    const uSampler = gl.getUniformLocation(prog, "uSampler");
    const uTime = gl.getUniformLocation(prog, "uTime");
    const uResolution = gl.getUniformLocation(prog, "uResolution");

    function resize() {
      const dpr = window.devicePixelRatio || 1;
      canvas!.width = wrapper!.clientWidth * dpr;
      canvas!.height = wrapper!.clientHeight * dpr;
    }
    resize();
    window.addEventListener("resize", resize);

    let start: number | null = null;
    function render(ts: number) {
      if (!start) start = ts;
      const dt = ts - start;

      gl!.texImage2D(gl!.TEXTURE_2D, 0, gl!.RGBA, gl!.RGBA, gl!.UNSIGNED_BYTE, src);
      gl!.viewport(0, 0, canvas!.width, canvas!.height);
      gl!.clearColor(0, 0, 0, 0);
      gl!.clear(gl!.COLOR_BUFFER_BIT);
      gl!.uniform1i(uSampler, 0);
      gl!.uniform1f(uTime, dt);
      gl!.uniform2f(uResolution, canvas!.width, canvas!.height);
      gl!.drawArrays(gl!.TRIANGLE_STRIP, 0, 4);

      frameRef.current = requestAnimationFrame(render);
    }

    frameRef.current = requestAnimationFrame(render);

    return () => {
      cancelAnimationFrame(frameRef.current);
      window.removeEventListener("resize", resize);
    };
  }, []);

  const layout = getButtonLayout(buttons.length);

  return (
    <div className="monitor-bezel" style={{ cursor: "default" }}>
      <div ref={wrapperRef} className="crt-wrapper">
        <canvas ref={canvasRef} className="crt-canvas" />
        <div className="crt-overlay">
          {buttons.map((btn, i) => {
            const pos = layout[i];
            return (
              <Link
                key={btn.to}
                to={btn.to}
                viewTransition
                className="crt-hitbox"
                style={{
                  left: `${(pos.x / CANVAS_W) * 100}%`,
                  top: `${(pos.y / CANVAS_H) * 100}%`,
                  width: `${(pos.w / CANVAS_W) * 100}%`,
                  height: `${(pos.h / CANVAS_H) * 100}%`,
                }}
                aria-label={btn.label}
              />
            );
          })}
          {children}
        </div>
      </div>
    </div>
  );
}
